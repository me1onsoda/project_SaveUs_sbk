package com.example.Ex02.dto;

public class MealEntryDto {
    public Long entryId;
    public Long userId;
    public String mealName;
    public String eatTime;        // "2025-11-12 14:30" 또는 "14:30"
    public Integer caloriesKcal;
    public Integer proteinG;
    public Integer carbsG;
    public Integer fatsG;
    public String createdAt;

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getEatTime() {
        return eatTime;
    }

    public void setEatTime(String eatTime) {
        this.eatTime = eatTime;
    }

    public Integer getCaloriesKcal() {
        return caloriesKcal;
    }

    public void setCaloriesKcal(Integer caloriesKcal) {
        this.caloriesKcal = caloriesKcal;
    }

    public Integer getProteinG() {
        return proteinG;
    }

    public void setProteinG(Integer proteinG) {
        this.proteinG = proteinG;
    }

    public Integer getCarbsG() {
        return carbsG;
    }

    public void setCarbsG(Integer carbsG) {
        this.carbsG = carbsG;
    }

    public Integer getFatsG() {
        return fatsG;
    }

    public void setFatsG(Integer fatsG) {
        this.fatsG = fatsG;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
