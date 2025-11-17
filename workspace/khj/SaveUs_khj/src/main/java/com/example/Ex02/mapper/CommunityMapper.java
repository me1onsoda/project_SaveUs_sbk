package com.example.Ex02.mapper;

import com.example.Ex02.dto.CommentDto;
import com.example.Ex02.dto.CommunityPostDto;
import com.example.Ex02.dto.TrendingPostDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommunityMapper {
    List<CommunityPostDto> selectPostList(@Param("currentUserId") Long currentUserId);
    List<TrendingPostDto> selectTrendingList();
    List<String> selectImagesByPostId(long postId);
    List<CommentDto> selectCommentsByPostId(long postId);
    //댓글 등록, 좋아요 토글 등의 메소드 추가필요
}