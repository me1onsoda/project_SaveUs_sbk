package com.example.Ex02.dto;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class CommentDto {
    private long commentId;
    private long postId;
    private long userId;
    private String content;
    private Timestamp createdAt;

    //사용자 정보
    private String authorNickname;
    private String authorProfileImageUrl;
    private String timeAgo;
}