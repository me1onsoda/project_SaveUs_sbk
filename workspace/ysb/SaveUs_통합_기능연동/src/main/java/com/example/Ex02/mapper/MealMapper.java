package com.example.Ex02.mapper;

import com.example.Ex02.dto.MealDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MealMapper {

    void saveMeal(Map<String, Object> map);

    MealDto findMealById(Long entryId);

    void updateMeal(MealDto dto);

    List<MealDto> findTodayMeals(Long userId);

    void deleteMeal(Long entryId);

    // 전날 영양소 합계 조회
    MealDto findYesterdayTotalNutrition(Long userId);
}
