package com.example.Ex02.controller;

import com.example.Ex02.dto.FoodNutritionDto;
import com.example.Ex02.service.FoodSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food")
public class FoodSearchController {

    @Autowired
    private FoodSearchService foodSearchService;

    /**
     * 음식 상세 검색 API
     * 예) GET /food/search?keyword=김치
     * 전체 정보(FoodNutritionDto) 반환
     */
    @GetMapping("/search")
    public List<FoodNutritionDto> searchFood(@RequestParam String keyword) {
        return foodSearchService.searchFood(keyword);
    }

    /**
     * 음식 자동완성 API
     * 예) GET /food/autocomplete?keyword=김
     * 음식명 목록(List<String>) 반환
     */
    @GetMapping("/autocomplete")
    public List<String> autocomplete(@RequestParam String keyword) {
        return foodSearchService.searchFoodNames(keyword);
    }
}
