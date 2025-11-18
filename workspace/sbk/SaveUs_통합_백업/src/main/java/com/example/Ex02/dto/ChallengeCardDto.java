package com.example.Ex02.dto;
import lombok.Data;

import java.util.List;

@Data
public class ChallengeCardDto {
    private Long challengeId;
    private String title;
    private String description;
    private String imageUrl;
    private boolean isUserParticipating;
    private List<TagDto> tags;
}