package com.example.Ex02.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DiabetesScoreDto {

    @JsonProperty("user_id")
    private Long userId;

    // --- [보낼 때 쓰는 데이터 (Input)] ---
    private int age;
    private int sex;          // 1: 남성, 2: 여성
    private double height;
    private double weight;

    // 어제 먹은 영양소 합계
    @JsonProperty("total_kcal")
    private double totalKcal;

    @JsonProperty("total_carbs")
    private double totalCarbs;

    @JsonProperty("total_fat")
    private double totalFat;

    @JsonProperty("total_protein")
    private double totalProtein;

    @JsonProperty("total_sodium")
    private double totalSodium;

    @JsonProperty("total_sugar")
    private double totalSugar;

    // --- [받을 때 쓰는 데이터 (Output)] ---
    // 파이썬이 계산해서 채워줄 필드들 (보낼 땐 null이거나 0)
    private Integer score;          // 점수 (0~100)
    private Integer similarity;     // 당뇨식단 유사도 (%)

    @JsonProperty("risk_level")
    private String riskLevel;       // GOOD, WARNING, DANGER

    private String error;           // 에러 메시지
}