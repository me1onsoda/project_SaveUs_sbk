package com.example.Ex02.dto;

import java.time.LocalDate;

// 하루 섭취 영양소 합산 결과
public class DailyIntakeDto {

    private LocalDate eatDate;   // 하루 날짜 (date → eatDate로 변경)
    private int calories;        // 총 칼로리
    private int protein;         // 총 단백질
    private int carbs;           // 총 탄수화물
    private int fats;            // 총 지방

    public LocalDate getEatDate() {
        return eatDate;
    }

    public void setEatDate(LocalDate eatDate) {
        this.eatDate = eatDate;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public int getCarbs() {
        return carbs;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public int getFats() {
        return fats;
    }

    public void setFats(int fats) {
        this.fats = fats;
    }
}
