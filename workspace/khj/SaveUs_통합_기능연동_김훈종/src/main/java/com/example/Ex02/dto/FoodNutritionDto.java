package com.example.Ex02.dto;

public class FoodNutritionDto {
    private Long foodId;
    private String foodName;
    private String category;
    private Integer caloriesKcal;
    private Integer carbsG;
    private Integer proteinG;
    private Integer fatG;
    private Integer sugarG;
    private Integer fiberG;
    private Integer calciumMg;
    private Integer sodiumMg;

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCaloriesKcal() {
        return caloriesKcal;
    }

    public void setCaloriesKcal(Integer caloriesKcal) {
        this.caloriesKcal = caloriesKcal;
    }

    public Integer getCarbsG() {
        return carbsG;
    }

    public void setCarbsG(Integer carbsG) {
        this.carbsG = carbsG;
    }

    public Integer getProteinG() {
        return proteinG;
    }

    public void setProteinG(Integer proteinG) {
        this.proteinG = proteinG;
    }

    public Integer getFatG() {
        return fatG;
    }

    public void setFatG(Integer fatG) {
        this.fatG = fatG;
    }

    public Integer getSugarG() {
        return sugarG;
    }

    public void setSugarG(Integer sugarG) {
        this.sugarG = sugarG;
    }

    public Integer getFiberG() {
        return fiberG;
    }

    public void setFiberG(Integer fiberG) {
        this.fiberG = fiberG;
    }

    public Integer getCalciumMg() {
        return calciumMg;
    }

    public void setCalciumMg(Integer calciumMg) {
        this.calciumMg = calciumMg;
    }

    public Integer getSodiumMg() {
        return sodiumMg;
    }

    public void setSodiumMg(Integer sodiumMg) {
        this.sodiumMg = sodiumMg;
    }
}
