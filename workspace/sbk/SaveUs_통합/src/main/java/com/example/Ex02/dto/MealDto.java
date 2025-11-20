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

    private Integer sugar;      // SUGAR_G
    private Integer fiber;      // FIBER_G
    private Integer calcium;    // CALCIUM_MG
    private Integer sodium;     // SODIUM_MG


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

    public Integer getSugar() { return sugar; }
    public void setSugar(Integer sugar) { this.sugar = sugar; }

    public Integer getFiber() { return fiber; }
    public void setFiber(Integer fiber) { this.fiber = fiber; }

    public Integer getCalcium() { return calcium; }
    public void setCalcium(Integer calcium) { this.calcium = calcium; }

    public Integer getSodium() { return sodium; }
    public void setSodium(Integer sodium) { this.sodium = sodium; }

    public String getEatTime() { return eatTime; }
    public void setEatTime(String eatTime) { this.eatTime = eatTime; }
}
