package com.example.Ex02.service;

import com.example.Ex02.dto.CommentDto;
import com.example.Ex02.dto.CommunityPostDto;
import com.example.Ex02.dto.PostRequestDto; // PostRequestDto 임포트
import com.example.Ex02.dto.TrendingPostDto;
import com.example.Ex02.mapper.CommunityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Transactional 임포트

import java.sql.Timestamp; // Timestamp import
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map; // Map 임포트

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    // 임시 현재 로그인 사용자 ID (나중에 Security Context에서 가져오도록 변경)
    private final Long TEMP_CURRENT_USER_ID = 1L;

    private final CommunityMapper communityMapper;

    @Override
    public List<CommunityPostDto> getPostList() {

        // [오류 수정] 파라미터(TEMP_CURRENT_USER_ID) 추가
        List<CommunityPostDto> postList = communityMapper.selectPostList(TEMP_CURRENT_USER_ID);

        for (CommunityPostDto post : postList) {
            List<String> imageUrls = communityMapper.selectImagesByPostId(post.getPostId());
            post.setImageUrls(imageUrls);

            String timeAgo = formatTimeAgo(post.getCreatedAt()); // 원본 시간으로 계산
            post.setTimeAgo(timeAgo); // 문자열(...분 전)로 덮어쓰기
        }

        return postList;
    }

    @Override
    public List<CommentDto> getCommentsByPostId(long postId) { //상세보기에 필요한 데이터
        List<CommentDto> commentList = communityMapper.selectCommentsByPostId(postId);

        for (CommentDto comment : commentList) {
            // TimeAgo 계산
            String timeAgo = formatTimeAgo(comment.getCreatedAt());
            comment.setTimeAgo(timeAgo);
        }

        return commentList;
    }

    @Override
    public List<TrendingPostDto> getTrendingList() {
        return communityMapper.selectTrendingList();
    }

    // [기능 추가] 새 게시물 작성
    @Override
    @Transactional
    public void createPost(PostRequestDto postRequestDto) {
        // 1. CommunityPostDto 객체 생성 및 기본 정보 설정
        CommunityPostDto post = new CommunityPostDto();
        post.setUserId(TEMP_CURRENT_USER_ID); // 임시 사용자 ID
        post.setContent(postRequestDto.getContent());

        // (임시) healthScore는 80으로 고정
        post.setHealthScore(80);

        // 2. POSTS 테이블에 기본 정보 삽입
        // (주의) insertPost는 postId를 post 객체에 다시 채워주도록 (useGeneratedKeys) 설정되어야 함
        communityMapper.insertPost(post);

        // 3. 만약 이미지 URL이 있다면 POST_IMAGES 테이블에 삽입 (현재는 Dto에 없음)
        // List<String> imageUrls = postRequestDto.getImageUrls();
        // if (imageUrls != null && !imageUrls.isEmpty()) {
        //     for (String url : imageUrls) {
        //         communityMapper.insertPostImage(post.getPostId(), url);
        //     }
        // }
    }

    // [기능 추가] 좋아요 토글
    @Override
    @Transactional
    public int toggleLike(long postId) {
        long userId = TEMP_CURRENT_USER_ID;

        // 1. 현재 좋아요 상태 확인
        boolean isLiked = communityMapper.isLikedByUser(postId, userId);

        if (isLiked) {
            // 2. 좋아요 상태면 -> 좋아요 취소 (삭제)
            communityMapper.deleteLike(postId, userId);
        } else {
            // 3. 좋아요 아니면 -> 좋아요 (삽입)
            communityMapper.insertLike(postId, userId);
        }

        // 4. 최신 좋아요 개수 반환
        return communityMapper.selectLikeCountByPostId(postId);
    }

    // [기능 추가] 댓글 작성
    @Override
    @Transactional
    public CommentDto createComment(long postId, String content) {
        long userId = TEMP_CURRENT_USER_ID;

        // 1. CommentDto 생성 및 DB 삽입
        CommentDto comment = new CommentDto();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);

        communityMapper.insertComment(comment); // useGeneratedKeys로 commentId가 채워져야 함

        // 2. 삽입된 댓글 정보 (방금 작성한 댓글) 다시 조회
        // (TimeAgo, Nickname 등 포함된 완전한 정보 반환을 위해)
        CommentDto newComment = communityMapper.selectCommentById(comment.getCommentId());

        // 3. TimeAgo 포맷팅
        newComment.setTimeAgo(formatTimeAgo(newComment.getCreatedAt()));

        return newComment;
    }


    private String formatTimeAgo(Timestamp createdAt) {
        if (createdAt == null) {
            return "";
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = createdAt.toLocalDateTime();
        Duration duration = Duration.between(past, now);
        long minutes = duration.toMinutes();
        if (minutes < 1) {
            return "Just now";
        }
        if (minutes < 60) {
            return minutes + " minutes ago";
        }
        long hours = duration.toHours();
        if (hours < 24) {
            return hours + " hours ago";
        }
        long days = duration.toDays();
        if (days < 30) {
            return days + " days ago";
        }
        // 30일 이상은 간단히 "YYYY.MM.DD" 형태로 반환 (선택 사항)
        return past.toLocalDate().toString().replace('-', '.');
    }
}
