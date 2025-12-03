package com.example.Ex02.dto;

import lombok.Data;

import java.util.List;

@Data
public class FoodRecommendResponse {

    private List<FoodItem> recommended;
    private List<String> deficit;

    @Data
    public static class FoodItem {
        private String food_name;
        private String category;
    }
}
