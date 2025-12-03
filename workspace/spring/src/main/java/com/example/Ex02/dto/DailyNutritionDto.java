package com.example.Ex02.dto;

public class DailyNutritionDto {
    public Long userId;
    public String intakeDate;   // "2025-11-12"
    public Integer caloriesKcal;
    public Integer proteinG;
    public Integer carbsG;
    public Integer fatsG;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(String intakeDate) {
        this.intakeDate = intakeDate;
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
}
