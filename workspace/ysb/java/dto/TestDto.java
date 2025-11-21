package com.ysb.library.dto;

import org.springframework.web.multipart.MultipartFile;

public class TestDto {
    private int food_id;
    private String food_name;
    private String category;
    private float calories_kcal;
    private Float carbs_g;
    private Float protein_g;
    private Float fat_g;
    private Float sugar_g;
    private Float fiber_g;
    private Float sodium_mg;
    private Float calcium_mg;

    private MultipartFile file;

    public TestDto() {
    }

    public TestDto(Float calcium_mg, float calories_kcal, Float carbs_g, String category, Float fat_g, Float fiber_g, int food_id, String food_name, Float protein_g, Float sodium_mg, Float sugar_g) {
        this.calcium_mg = calcium_mg;
        this.calories_kcal = calories_kcal;
        this.carbs_g = carbs_g;
        this.category = category;
        this.fat_g = fat_g;
        this.fiber_g = fiber_g;
        this.food_id = food_id;
        this.food_name = food_name;
        this.protein_g = protein_g;
        this.sodium_mg = sodium_mg;
        this.sugar_g = sugar_g;
    }

    public Float getCalcium_mg() {
        return calcium_mg;
    }

    public void setCalcium_mg(Float calcium_mg) {
        this.calcium_mg = calcium_mg;
    }

    public float getCalories_kcal() {
        return calories_kcal;
    }

    public void setCalories_kcal(float calories_kcal) {
        this.calories_kcal = calories_kcal;
    }

    public Float getCarbs_g() {
        return carbs_g;
    }

    public void setCarbs_g(Float carbs_g) {
        this.carbs_g = carbs_g;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Float getFat_g() {
        return fat_g;
    }

    public void setFat_g(Float fat_g) {
        this.fat_g = fat_g;
    }

    public Float getFiber_g() {
        return fiber_g;
    }

    public void setFiber_g(Float fiber_g) {
        this.fiber_g = fiber_g;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public int getFood_id() {
        return food_id;
    }

    public void setFood_id(int food_id) {
        this.food_id = food_id;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public Float getProtein_g() {
        return protein_g;
    }

    public void setProtein_g(Float protein_g) {
        this.protein_g = protein_g;
    }

    public Float getSodium_mg() {
        return sodium_mg;
    }

    public void setSodium_mg(Float sodium_mg) {
        this.sodium_mg = sodium_mg;
    }

    public Float getSugar_g() {
        return sugar_g;
    }

    public void setSugar_g(Float sugar_g) {
        this.sugar_g = sugar_g;
    }

    @Override
    public String toString() {
        return "TestDto{" +
                ", food_id=" + food_id +
                ", food_name='" + food_name + '\'' +
                ", category='" + category + '\'' +
                ", calories_kcal=" + calories_kcal +
                ", carbs_g=" + carbs_g +
                ", protein_g=" + protein_g +
                ", fat_g=" + fat_g +
                ", sugar_g=" + sugar_g +
                ", fiber_g=" + fiber_g +
                "calcium_mg=" + calcium_mg +
                ", sodium_mg=" + sodium_mg +
                '}';
    }
}
