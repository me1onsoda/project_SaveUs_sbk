package com.example.Ex02.dto;
import java.time.LocalDateTime;


public class MealDto {

    private Long entryId;
    private Long userId;

    private String mealName;

    // 화면 표시용 HH:mm
    private String mealTime;

    private Integer calories;
    private Integer protein;
    private Integer carbs;
    private Integer fat;

    private Integer sugar;      // SUGAR_G
    private Integer fiber;      // FIBER_G
    private Integer calcium;    // CALCIUM_MG
    private Integer sodium;     // SODIUM_MG

    // 실제 DB 저장 시간(LocalDateTime)
    private LocalDateTime eatTime;

    // 시간대별 영양소
    private Integer caloriesBreakfast; // 아침 칼로리 (05~11시)
    private Integer proteinBreakfast;  // 아침 단백질
    private Integer caloriesLunch;     // 점심 칼로리
    private Integer caloriesDinner;    // 저녁 칼로리
    private Integer caloriesLateNight; // 야식 칼로리 (21~04시)

    private Integer mealCount;


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

    public LocalDateTime getEatTime() { return eatTime; }
    public void setEatTime(LocalDateTime eatTime) { this.eatTime = eatTime; }

    public Integer getCaloriesBreakfast() {
        return caloriesBreakfast;
    }

    public void setCaloriesBreakfast(Integer caloriesBreakfast) {
        this.caloriesBreakfast = caloriesBreakfast;
    }

    public Integer getProteinBreakfast() {
        return proteinBreakfast;
    }

    public void setProteinBreakfast(Integer proteinBreakfast) {
        this.proteinBreakfast = proteinBreakfast;
    }

    public Integer getCaloriesLunch() {
        return caloriesLunch;
    }

    public void setCaloriesLunch(Integer caloriesLunch) {
        this.caloriesLunch = caloriesLunch;
    }

    public Integer getCaloriesDinner() {
        return caloriesDinner;
    }

    public void setCaloriesDinner(Integer caloriesDinner) {
        this.caloriesDinner = caloriesDinner;
    }

    public Integer getCaloriesLateNight() {
        return caloriesLateNight;
    }

    public void setCaloriesLateNight(Integer caloriesLateNight) {
        this.caloriesLateNight = caloriesLateNight;
    }

    public Integer getMealCount() {
        return mealCount;
    }

    public void setMealCount(Integer mealCount) {
        this.mealCount = mealCount;
    }

    @Override
    public String toString() {
        return "MealDto{" +
                "entryId=" + entryId +
                ", userId=" + userId +
                ", mealName='" + mealName + '\'' +
                ", mealTime='" + mealTime + '\'' +
                ", calories=" + calories +
                ", protein=" + protein +
                ", carbs=" + carbs +
                ", fat=" + fat +
                ", sugar=" + sugar +
                ", fiber=" + fiber +
                ", calcium=" + calcium +
                ", sodium=" + sodium +
                ", eatTime=" + eatTime +
                '}';
    }
}
