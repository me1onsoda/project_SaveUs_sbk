package com.example.Ex02.dto;

public class UserGoalDto {
    private Long goalId;
    private Long userId;
    private Integer caloriesKcal;
    private Integer proteinG;
    private Integer carbsG;
    private Integer fatsG;

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
