package com.example.Ex02.dto;

import java.util.List;

public class MainPageResponseDto {
    public UserMainDto user;
    public DailyNutritionDto dailyNutrition;
    public UserGoalDto userGoal;
    public List<MealEntryDto> todayMeals;
    public List<NutrientDetailDto> nutrientDetails;
    public HealthScoreDailyDto todayScore;
}
