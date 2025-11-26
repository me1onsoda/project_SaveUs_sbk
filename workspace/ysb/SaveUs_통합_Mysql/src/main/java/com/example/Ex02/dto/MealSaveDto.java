package com.example.Ex02.dto;

public class MealSaveDto {

    private String mealName;
    private String mealTime; // HH:mm 입력값

    private Integer calories;
    private Integer carbs;
    private Integer protein;
    private Integer fat;

    private Integer sugar;
    private Integer fiber;
    private Integer calcium;
    private Integer sodium;

    private Long userId;


    public String getMealName() { return mealName; }
    public void setMealName(String mealName) { this.mealName = mealName; }

    public String getMealTime() { return mealTime; }
    public void setMealTime(String mealTime) { this.mealTime = mealTime; }

    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }

    public Integer getCarbs() { return carbs; }
    public void setCarbs(Integer carbs) { this.carbs = carbs; }

    public Integer getProtein() { return protein; }
    public void setProtein(Integer protein) { this.protein = protein; }

    public Integer getFat() { return fat; }
    public void setFat(Integer fat) { this.fat = fat; }

    public Integer getSugar() { return sugar; }
    public void setSugar(Integer sugar) { this.sugar = sugar; }

    public Integer getFiber() { return fiber; }
    public void setFiber(Integer fiber) { this.fiber = fiber; }

    public Integer getCalcium() { return calcium; }
    public void setCalcium(Integer calcium) { this.calcium = calcium; }

    public Integer getSodium() { return sodium; }
    public void setSodium(Integer sodium) { this.sodium = sodium; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
