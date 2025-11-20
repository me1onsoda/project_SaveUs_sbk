package com.example.Ex02.mapper;

import com.example.Ex02.dto.CommentDto;
import com.example.Ex02.dto.CommunityPostDto;
import com.example.Ex02.dto.TrendingPostDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommunityMapper {
    List<CommunityPostDto> selectPostList(@Param("currentUserId") Long currentUserId,@Param("persona") String persona);
    List<TrendingPostDto> selectTrendingList();
    List<String> selectImagesByPostId(long postId);
    List<CommentDto> selectCommentsByPostId(long postId);
    void insertPost(CommunityPostDto post);
    void insertPostImage(@Param("postId") long postId,
                         @Param("imageUrl") String imageUrl,
                         @Param("displayOrder") int displayOrder);
    boolean isLikedByUser(@Param("postId") long postId, @Param("userId") long userId);
    void insertLike(@Param("postId") long postId, @Param("userId") long userId);
    void deleteLike(@Param("postId") long postId, @Param("userId") long userId);
    int selectLikeCountByPostId(@Param("postId") long postId);
    void insertComment(CommentDto comment);
    CommentDto selectCommentById(@Param("commentId") long commentId);

    //  1. 게시물 단건 조회 (본인 확인 및 수정 후 반환 시 필요)
    CommunityPostDto selectPostById(@Param("postId") long postId);
    int selectCommentCountByPostId(@Param("postId") long postId); // 좋아요 카운터와 짝 맞춤

    //  2. 게시물 삭제
    void deletePost(@Param("postId") long postId);

    //  3. 게시물 수정 (텍스트)
    void updatePostContent(@Param("postId") long postId, @Param("content") String content);

    //  4. 게시물 이미지 삭제 (수정 시)
    void deletePostImages(@Param("postId") long postId);

    //  5. 댓글 삭제
    void deleteComment(@Param("commentId") long commentId);

    //  6. 댓글 수정
    void updateCommentContent(@Param("commentId") long commentId, @Param("content") String content);

    //  3단계: URL 기준으로 이미지 1개 삭제
    void deletePostImageByUrl(@Param("postId") long postId, @Param("imageUrl") String imageUrl);

    // 3단계: 새 이미지의 displayOrder를 정하기 위해 현재 최대값 조회
    Integer selectMaxDisplayOrder(@Param("postId") long postId);

    List<CommunityPostDto> selectPopularPostList(@Param("currentUserId") Long currentUserId,@Param("persona") String persona);
    List<String> selectDistinctPersonas();
}