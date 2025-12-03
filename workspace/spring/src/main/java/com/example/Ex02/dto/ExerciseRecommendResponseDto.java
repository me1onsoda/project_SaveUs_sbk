package com.example.Ex02.dto;

import lombok.Data;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExerciseRecommendResponseDto {

    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("predicted_category")
    private String predictedCategory;

    @JsonProperty("routine")
    private Routine routine;

    @Data
    public static class Routine {

        @JsonProperty("준비운동")
        private ExerciseItem warmup;

        @JsonProperty("본운동")
        private ExerciseItem main;

        @JsonProperty("정리운동")
        private ExerciseItem cool;
    }

    @Data
    public static class ExerciseItem {
        private String name;
        private String url;
        private String title;
        private String thumb;
    }
}


