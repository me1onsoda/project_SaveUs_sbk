package com.example.Ex02.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// 파이썬 서버의 응답(1개 음식 정보)을 받는 객체
@Data
public class AiDto {

    @JsonProperty("food_name")
    private String foodName;

    @JsonProperty("calories_kcal")
    private Double calories;

    @JsonProperty("carbs_g")
    private Double carbs;

    @JsonProperty("protein_g")
    private Double protein;

    @JsonProperty("fat_g")
    private Double fat;

    @JsonProperty("sugar_g")
    private Double sugar;

    @JsonProperty("fiber_g")
    private Double fiber;

    @JsonProperty("sodium_mg")
    private Double sodium;

    @JsonProperty("calcium_mg")
    private Double calcium;
}