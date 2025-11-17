package com.example.Ex02.service;

import com.example.Ex02.dto.CommentDto;
import com.example.Ex02.dto.CommunityPostDto;
import com.example.Ex02.dto.PostRequestDto;
import com.example.Ex02.dto.TrendingPostDto;
import org.springframework.web.multipart.MultipartFile; // 임포트

import java.io.IOException;
import java.util.List;

public interface CommunityService {
    List<CommunityPostDto> getPostList(Long currentUserId);

    List<CommentDto> getCommentsByPostId(long postId);

    List<TrendingPostDto> getTrendingList();

    void createPost(PostRequestDto postRequestDto, Long currentUserId) throws IOException;

    int toggleLike(long postId, Long currentUserId);

    CommentDto createComment(long postId, String content, Long currentUserId);

    void deletePost(long postId, Long currentUserId);

    void deleteComment(long commentId, Long currentUserId);

    CommunityPostDto updatePost(long postId, Long currentUserId, String content,
                                List<MultipartFile> newImages, List<String> imagesToDelete) throws IOException;

    CommentDto updateComment(long commentId, Long currentUserId, String content);

    List<CommunityPostDto> getPopularPostList(Long currentUserId);
}