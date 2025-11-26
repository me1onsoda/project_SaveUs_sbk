package com.example.Ex02.dto;

import lombok.Data;

@Data
public class MyChallengeItemDto {
    private Long challengeId;
    private String title;
    private String description;
    private String imageUrl; // 챌린지 아이콘/이미지
    private int progressPercent; // User_Challenges.PROGRESS_PERCENT

    private Long userChallengeId;   // PK (업데이트할 때 필요) //USERS_CHALLENGES 테이블
    private String status;          // ONGOING, COMPLETED
    private Double startValue;      // 시작 체중 등 (체중 감량 챌린지용)
    private Integer currentCount;   // 현재 성공 횟수 (스케줄러용)

    private String challengeType;   // LIMIT, MINIMUM, CHECK
    private String metricKey;       // sodium, carbs...
    private Double targetValue;     // 2000, 60...
    private Integer durationDays;   // 챌린지 기간
    private Long userId;
    private Integer points;         // 챌린지 성공 시 획득 포인트
    private Integer successCount;   // 챌린지 총 성공횟수
}
