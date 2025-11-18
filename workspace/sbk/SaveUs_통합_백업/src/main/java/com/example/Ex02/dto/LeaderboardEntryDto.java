package com.example.Ex02.dto;

import lombok.Data;

@Data
public class LeaderboardEntryDto {
    private String userNickname;
    private String userProfileImg;
    private int score;

}