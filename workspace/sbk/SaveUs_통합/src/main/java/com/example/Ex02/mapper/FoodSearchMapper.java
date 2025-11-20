package com.example.Ex02.mapper;

import com.example.Ex02.dto.FoodNutritionDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FoodSearchMapper {
    List<FoodNutritionDto> searchFood(String keyword);

    List<String> searchFoodNames(String keyword);
}
