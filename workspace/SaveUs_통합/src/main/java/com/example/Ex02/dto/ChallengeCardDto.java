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
    private String challengeType;   // LIMIT, MINIMUM, CHECK
    private String metricKey;       // sodium, sugar, weight_loss 등
    private Double targetValue;     // 2000, 60, -1 등 (기준값)
    private Integer durationDays;   // 3 (3일 연속 등)
    private Integer points;         //챌린지 성공시 획득 포인트
}