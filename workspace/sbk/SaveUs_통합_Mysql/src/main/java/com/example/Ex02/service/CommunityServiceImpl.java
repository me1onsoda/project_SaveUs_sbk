package com.example.Ex02.service;

import com.example.Ex02.dto.CommentDto;
import com.example.Ex02.dto.CommunityPostDto;
import com.example.Ex02.dto.PostRequestDto;
import com.example.Ex02.dto.TrendingPostDto;
import com.example.Ex02.mapper.CommunityMapper;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityMapper communityMapper;
    private final String UPLOAD_DIR = "/home/ubuntu/uploads/posts/";


    @Override
    public List<CommunityPostDto> getPostList(Long currentUserId, String persona) {
        List<CommunityPostDto> postList = communityMapper.selectPostList(currentUserId, persona);
        for (CommunityPostDto post : postList) {
            List<String> imageUrls = communityMapper.selectImagesByPostId(post.getPostId());
            post.setImageUrls(imageUrls);
            post.setTimeAgo(formatTimeAgo(post.getCreatedAt()));
        }
        return postList;
    }

    @Override
    public List<CommentDto> getCommentsByPostId(long postId) {
        List<CommentDto> commentList = communityMapper.selectCommentsByPostId(postId);
        for (CommentDto comment : commentList) {
            comment.setTimeAgo(formatTimeAgo(comment.getCreatedAt()));
        }
        return commentList;
    }

    @Override
    public List<TrendingPostDto> getTrendingList() {
        return communityMapper.selectTrendingList();
    }

    @Override
    @Transactional
    public void createPost(PostRequestDto postRequestDto, Long currentUserId) throws IOException {
        // DTO 생성 및 텍스트 정보 삽입
        CommunityPostDto post = new CommunityPostDto();
        post.setUserId(currentUserId);
        post.setContent(postRequestDto.getContent());
        int healthScore = communityMapper.getHealthScore(currentUserId);
        post.setHealthScore(healthScore);
        communityMapper.insertPost(post);

        long postId = post.getPostId(); // DB에서 생성된 ID 받아오기

        // 이미지 파일 처리
        List<MultipartFile> images = postRequestDto.getImages();
        if (images != null && !images.isEmpty()) {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            int displayOrder = 1;
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String storedFileName = UUID.randomUUID().toString() + image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
                    Path destinationPath = Paths.get(UPLOAD_DIR + storedFileName);
                    image.transferTo(destinationPath);
                    String imageUrl = "/uploads/posts/" + storedFileName;
                    communityMapper.insertPostImage(postId, imageUrl, displayOrder++);
                }
            }
        }
    }

    @Override
    @Transactional
    public int toggleLike(long postId, Long currentUserId) {
        boolean isLiked = communityMapper.isLikedByUser(postId, currentUserId);
        if (isLiked) {
            communityMapper.deleteLike(postId, currentUserId);
        } else {
            communityMapper.insertLike(postId, currentUserId);
        }
        return communityMapper.selectLikeCountByPostId(postId);
    }

    @Override
    @Transactional
    public CommentDto createComment(long postId, String content, Long currentUserId) {
        CommentDto comment = new CommentDto();
        comment.setPostId(postId);
        comment.setUserId(currentUserId);
        comment.setContent(content);

        communityMapper.insertComment(comment);

        // 작성 직후 전체 정보(닉네임 등)를 다시 조회
        CommentDto newComment = communityMapper.selectCommentById(comment.getCommentId());
        newComment.setTimeAgo(formatTimeAgo(newComment.getCreatedAt()));
        return newComment;
    }


    // --- 수정/삭제 로직 (본인 확인 로직 수정) ---

    @Override
    @Transactional
    public void deletePost(long postId, Long currentUserId) {
        CommunityPostDto post = communityMapper.selectPostById(postId);
        if (post == null) {
            throw new RuntimeException("Post not found");
        }
        if (currentUserId == null || post.getUserId() != currentUserId) {
//            throw new AccessDeniedException("You are not the owner of this post");
        }

        List<String> imageUrls = communityMapper.selectImagesByPostId(postId);
        for (String url : imageUrls) {
            deleteFile(url);
        }
        communityMapper.deletePost(postId);
    }

    @Override
    @Transactional
    public void deleteComment(long commentId, Long currentUserId) {
        CommentDto comment = communityMapper.selectCommentById(commentId);
        if (comment == null) {
            throw new RuntimeException("Comment not found");
        }
        if (currentUserId == null || comment.getUserId() != currentUserId) {
//            throw new AccessDeniedException("You are not the owner of this comment");
        }
        communityMapper.deleteComment(commentId);
    }

    @Override
    @Transactional
    public CommunityPostDto updatePost(long postId, Long currentUserId, String content,
                                       List<MultipartFile> newImages, List<String> imagesToDelete) throws IOException {

        // 게시물 정보 조회 및 본인 확인
        CommunityPostDto post = communityMapper.selectPostById(postId);
        if (post == null) {
            throw new RuntimeException("Post not found");
        }
//        if (currentUserId == null || post.getUserId() != currentUserId) {
//            throw new AccessDeniedException("You are not the owner of this post");
//        }

        // 텍스트 내용(content)
        communityMapper.updatePostContent(postId, content);

        // '삭제 요청된' 이미지 처리 (imagesToDelete)
        if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
            for (String imageUrl : imagesToDelete) {
                // 로컬 파일 삭제
                deleteFile(imageUrl);
                // DB에서 삭제
                communityMapper.deletePostImageByUrl(postId, imageUrl);
            }
        }

        // '새로 추가된' 이미지 처리 (newImages)
        if (newImages != null && !newImages.isEmpty() && !newImages.get(0).isEmpty()) {

            // 새 이미지의 순서(displayOrder)를 정하기 위해 현재 최대값 조회
            Integer maxOrder = communityMapper.selectMaxDisplayOrder(postId);
            int displayOrder = (maxOrder == null) ? 1 : maxOrder + 1;

            // 새 이미지 업로드 및 DB에 저장
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            for (MultipartFile image : newImages) {
                if (!image.isEmpty()) {
                    String storedFileName = UUID.randomUUID().toString() + image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
                    Path destinationPath = Paths.get(UPLOAD_DIR + storedFileName);
                    image.transferTo(destinationPath);
                    String imageUrl = "/uploads/posts/" + storedFileName;
                    communityMapper.insertPostImage(postId, imageUrl, displayOrder++);
                }
            }
        }

        // 최신 게시물 정보 다시 조회하여 반환
        CommunityPostDto updatedPost = communityMapper.selectPostById(postId);
        updatedPost.setImageUrls(communityMapper.selectImagesByPostId(postId));
        updatedPost.setTimeAgo(formatTimeAgo(updatedPost.getCreatedAt()));
        updatedPost.setLikeCount(communityMapper.selectLikeCountByPostId(postId));
        updatedPost.setCommentCount(communityMapper.selectCommentCountByPostId(postId));
        updatedPost.setLikedByMe(communityMapper.isLikedByUser(postId, currentUserId));

        return updatedPost;
    }

    @Override
    @Transactional
    public CommentDto updateComment(long commentId, Long currentUserId, String content) {
        CommentDto comment = communityMapper.selectCommentById(commentId);
        if (comment == null) {
            throw new RuntimeException("Comment not found");
        }
        if (currentUserId == null || comment.getUserId() != currentUserId) {
//            throw new AccessDeniedException("You are not the owner of this comment");
        }

        communityMapper.updateCommentContent(commentId, content);

        CommentDto updatedComment = communityMapper.selectCommentById(commentId);
        updatedComment.setTimeAgo(formatTimeAgo(updatedComment.getCreatedAt()));
        return updatedComment;
    }


    // --- Helper Methods ---
    private void deleteFile(String webUrl) {
        if (webUrl == null || !webUrl.startsWith("/uploads/posts/")) {
            return;
        }
        try {
            String fileName = webUrl.substring("/uploads/posts/".length());
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            File file = filePath.toFile();
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            System.err.println("Failed to delete file: " + webUrl + " | Error: " + e.getMessage());
        }
    }

    @Override
    public List<CommunityPostDto> getPopularPostList(Long currentUserId, String persona) {
        // DB에서 오늘 인기순 목록 조회
        List<CommunityPostDto> postList = communityMapper.selectPopularPostList(currentUserId,persona);

        // 각 게시물에 이미지 URL과 TimeAgo 포맷 적용 (getPostList와 동일)
        for (CommunityPostDto post : postList) {
            List<String> imageUrls = communityMapper.selectImagesByPostId(post.getPostId());
            post.setImageUrls(imageUrls);
            post.setTimeAgo(formatTimeAgo(post.getCreatedAt()));
        }
        return postList;
    }

    @Override
    public List<String> getDistinctPersonas() {
        return communityMapper.selectDistinctPersonas();
    }

    private String formatTimeAgo(Timestamp createdAt) {
        if (createdAt == null) return "";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = createdAt.toLocalDateTime();
        Duration duration = Duration.between(past, now);
        long minutes = duration.toMinutes();
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " minutes ago";
        long hours = duration.toHours();
        if (hours < 24) return hours + " hours ago";
        long days = duration.toDays();
        if (days < 30) return days + " days ago";
        return past.toLocalDate().toString().replace('-', '.');
    }

    @Override
    @Transactional
    public int getCommentCount(long postId){
        return communityMapper.selectCommentCountByPostId(postId);
    }

    @Override
    @Transactional
    public boolean isExistPost(long postId){
        return communityMapper.isExistPost(postId);
    }
}