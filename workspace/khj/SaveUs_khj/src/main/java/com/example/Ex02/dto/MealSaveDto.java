package com.example.Ex02.dto;

public class MealSaveDto {
    private String mealName;
    private String mealTime;
    private Integer calories;
    private Integer carbs;
    private Integer protein;
    private Integer fat;

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getMealTime() {
        return mealTime;
    }

    public void setMealTime(String mealTime) {
        this.mealTime = mealTime;
    }

    public Integer getCalories() { // ← 수정됨
        return calories;
    }

    public void setCalories(Integer calories) { // ← 수정됨
        this.calories = calories;
    }

    public Integer getCarbs() {
        return carbs;
    }

    public void setCarbs(Integer carbs) {
        this.carbs = carbs;
    }

    public Integer getProtein() {
        return protein;
    }

    public void setProtein(Integer protein) {
        this.protein = protein;
    }

    public Integer getFat() {
        return fat;
    }

    public void setFat(Integer fat) {
        this.fat = fat;
    }
}
