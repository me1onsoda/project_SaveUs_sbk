package com.example.Ex02.service;

import com.example.Ex02.dto.CommentDto;
import com.example.Ex02.dto.CommunityPostDto;
import com.example.Ex02.dto.PostRequestDto;
import com.example.Ex02.dto.TrendingPostDto;

import java.util.List;

public interface CommunityService {
    List<CommunityPostDto> getPostList();

    List<CommentDto> getCommentsByPostId(long postId);

    List<TrendingPostDto> getTrendingList();
    // [기능 추가]
    void createPost(PostRequestDto postRequestDto);

    int toggleLike(long postId);

    CommentDto createComment(long postId, String content);
}