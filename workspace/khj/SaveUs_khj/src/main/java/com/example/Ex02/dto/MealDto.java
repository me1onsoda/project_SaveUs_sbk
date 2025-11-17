package com.example.Ex02.dto;

public class MealDto {

    private Long entryId;
    private Long userId;

    private String mealName;

    // "HH:mm"
    private String mealTime;

    private Integer calories;

    private Integer protein;
    private Integer carbs;
    private Integer fat;

    private String eatTime; // LocalDateTime → 문자열 형태

    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getMealName() { return mealName; }
    public void setMealName(String mealName) { this.mealName = mealName; }

    public String getMealTime() { return mealTime; }
    public void setMealTime(String mealTime) { this.mealTime = mealTime; }

    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }

    public Integer getProtein() { return protein; }
    public void setProtein(Integer protein) { this.protein = protein; }

    public Integer getCarbs() { return carbs; }
    public void setCarbs(Integer carbs) { this.carbs = carbs; }

    public Integer getFat() { return fat; }
    public void setFat(Integer fat) { this.fat = fat; }

    public String getEatTime() { return eatTime; }
    public void setEatTime(String eatTime) { this.eatTime = eatTime; }
}
