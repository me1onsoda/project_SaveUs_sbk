package com.example.Ex02.service;

import com.example.Ex02.dto.CommentDto;
import com.example.Ex02.dto.CommunityPostDto;
import com.example.Ex02.dto.TrendingPostDto;

import java.util.List;

public interface CommunityService {
    List<CommunityPostDto> getPostList();

    List<CommentDto> getCommentsByPostId(long postId);

    List<TrendingPostDto> getTrendingList();
}