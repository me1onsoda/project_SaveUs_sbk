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

    void insertPost(CommunityPostDto post);
    // void insertPostImage(@Param("postId") long postId, @Param("imageUrl") String imageUrl);

    boolean isLikedByUser(@Param("postId") long postId, @Param("userId") long userId);
    void insertLike(@Param("postId") long postId, @Param("userId") long userId);
    void deleteLike(@Param("postId") long postId, @Param("userId") long userId);
    int selectLikeCountByPostId(@Param("postId") long postId);

    void insertComment(CommentDto comment);
    CommentDto selectCommentById(@Param("commentId") long commentId);
}
