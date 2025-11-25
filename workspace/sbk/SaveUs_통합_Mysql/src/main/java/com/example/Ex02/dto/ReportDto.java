package com.example.Ex02.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReportDto {
    // [기본] 데이터 존재 여부 (화면 '기록 없음' 표시용)
    private boolean hasData;

    // 1. [차트] 건강 점수 추이 (X축: 날짜, Y축: 점수)
    private List<String> scoreDates;
    private List<Integer> scoreValues;
    private int averageScore;

    // 2. [차트] 식단 레이더 차트 (순서: 탄,단,지,당,식이)
    private List<Integer> radarMyIntake; // 나의 섭취 %
    private List<Integer> radarGoal;     // 권장 섭취 %

    // 3. [차트] 비만 위험도
    private double obesityProbability;   // 0 ~ 100

    // 4. [텍스트] 당뇨 예측 결과
    private int diabetesScore;           // 점수
    private int diabetesSimilarity;      // 유사도 %
    private String diabetesRiskLevel;    // 위험 단계 (GOOD, WARNING, DANGER)
    private String diabetesComment;      // AI 멘트

    // 5. [차트] 식단 유형 변화 (X축: 날짜, Y축: 코드값)
    private List<String> dietDates;      // 날짜 (X축 공통)
    private List<Integer> carbCodes;     // 탄수화물 코드 (1:저, 2:균, 3:고)
    private List<Integer> proteinCodes;  // 단백질 코드
    private List<Integer> fatCodes;      // 지방 코드

    // 6. [리스트] 자주 먹은 음식 Top 5 (기존 MealDto 재사용)
    private List<MealDto> topMeals;
}