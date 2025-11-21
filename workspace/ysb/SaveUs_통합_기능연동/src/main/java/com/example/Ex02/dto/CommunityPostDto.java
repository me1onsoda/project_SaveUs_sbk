package com.example.Ex02.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class CommunityPostDto {

    // POSTS 테이블
    private long postId;
    private long userId; //작성자
    private String content;
//    private String imageUrl; //게시글 이미지
    private List<String> imageUrls;
    private int healthScore;
    private Timestamp createdAt; // 원본 날짜
    private String timeAgo; // (Service에서 "...분 전"으로 가공해서)

    // USERS 테이블 (JOIN)
    private String authorNickname;
    private String authorProfileImageUrl;
    private String authorPersona;

    // LIKES/COMMENTS 테이블 (SubQuery 또는 COUNT)
    private int likeCount;
    private int commentCount;
    private boolean likedByMe;
}
