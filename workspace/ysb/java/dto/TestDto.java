package com.ysb.library.dto;

import org.springframework.web.multipart.MultipartFile;

// 검출된 음식 영양 데이터
public class TestDto {
    private int foodId;
    private String foodName;
    private String category;
    private int caloriesKcal;
    private Integer carbsG;
    private Integer proteinG;
    private Integer fatG;
    private Integer sugarG;
    private Integer fiberG;
    private Integer sodiumMg;
    private Integer calciumMg;

    private MultipartFile file;

    public TestDto() {
    }

    public TestDto(Integer calciumMg, int caloriesKcal, Integer carbsG, String category, Integer fatG, Integer fiberG, int foodId, String foodName, Integer proteinG, Integer sodiumMg, Integer sugarG) {
        this.calciumMg = calciumMg;
        this.caloriesKcal = caloriesKcal;
        this.carbsG = carbsG;
        this.category = category;
        this.fatG = fatG;
        this.fiberG = fiberG;
        this.foodId = foodId;
        this.foodName = foodName;
        this.proteinG = proteinG;
        this.sodiumMg = sodiumMg;
        this.sugarG = sugarG;
    }

    public Integer getCalciumMg() {
        return calciumMg;
    }

    public void setCalciumMg(Integer calciumMg) {
        this.calciumMg = calciumMg;
    }

    public int getCaloriesKcal() {
        return caloriesKcal;
    }

    public void setCaloriesKcal(int caloriesKcal) {
        this.caloriesKcal = caloriesKcal;
    }

    public Integer getCarbsG() {
        return carbsG;
    }

    public void setCarbsG(Integer carbsG) {
        this.carbsG = carbsG;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getFatG() {
        return fatG;
    }

    public void setFatG(Integer fatG) {
        this.fatG = fatG;
    }

    public Integer getFiberG() {
        return fiberG;
    }

    public void setFiberG(Integer fiberG) {
        this.fiberG = fiberG;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Integer getProteinG() {
        return proteinG;
    }

    public void setProteinG(Integer proteinG) {
        this.proteinG = proteinG;
    }

    public Integer getSodiumMg() {
        return sodiumMg;
    }

    public void setSodiumMg(Integer sodiumMg) {
        this.sodiumMg = sodiumMg;
    }

    public Integer getSugarG() {
        return sugarG;
    }

    public void setSugarG(Integer sugarG) {
        this.sugarG = sugarG;
    }

    @Override
    public String toString() {
        return "TestDto{" +
                "calciumMg=" + calciumMg +
                ", foodId=" + foodId +
                ", foodName='" + foodName + '\'' +
                ", category='" + category + '\'' +
                ", caloriesKcal=" + caloriesKcal +
                ", carbsG=" + carbsG +
                ", proteinG=" + proteinG +
                ", fatG=" + fatG +
                ", sugarG=" + sugarG +
                ", fiberG=" + fiberG +
                ", sodiumMg=" + sodiumMg +
                '}';
    }
}
