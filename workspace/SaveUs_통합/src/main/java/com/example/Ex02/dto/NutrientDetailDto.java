package com.example.Ex02.dto;

public class NutrientDetailDto {
    public Long nutrientId;
    public Long entryId;
    public String nutrientName;
    public Double amountMg;
    public String createdAt;

    public Long getNutrientId() {
        return nutrientId;
    }

    public void setNutrientId(Long nutrientId) {
        this.nutrientId = nutrientId;
    }

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public String getNutrientName() {
        return nutrientName;
    }

    public void setNutrientName(String nutrientName) {
        this.nutrientName = nutrientName;
    }

    public Double getAmountMg() {
        return amountMg;
    }

    public void setAmountMg(Double amountMg) {
        this.amountMg = amountMg;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
