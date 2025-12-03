package com.example.Ex02.dto;

import lombok.Data;

@Data
public class TrendingPostDto {

    private long postId; // 게시물 상세 링크용 ID -> 상세보기 페이지까지??
    private String authorNickname;
    private String summary; // 내용 50자 이내로 자른 요약

}