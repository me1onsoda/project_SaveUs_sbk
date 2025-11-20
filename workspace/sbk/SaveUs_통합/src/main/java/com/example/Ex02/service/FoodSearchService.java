package com.example.Ex02.service;

import com.example.Ex02.dto.FoodNutritionDto;
import com.example.Ex02.mapper.FoodSearchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodSearchService {

    @Autowired
    private FoodSearchMapper foodSearchMapper;

    /**
     * 음식명으로 검색
     * @param keyword 검색어
     * @return 음식 리스트
     */
    public List<FoodNutritionDto> searchFood(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        return foodSearchMapper.searchFood(keyword);
    }
}
