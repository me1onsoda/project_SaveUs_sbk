package com.example.Ex02.dto;

import lombok.Data;

@Data
public class FoodRecommendRequest {

    private double goal_calories;
    private double goal_carbs;
    private double goal_protein;
    private double goal_fat;

    private double current_calories;
    private double current_carbs;
    private double current_protein;
    private double current_fat;
    private double current_fiber;
    private double current_sodium;

    private int user_id;
}
