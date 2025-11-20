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
     * 음식 검색 API
     * 예) GET /food/search?keyword=김치
     */
    @GetMapping("/search")
    public List<FoodNutritionDto> searchFood(@RequestParam String keyword) {
        return foodSearchService.searchFood(keyword);
    }
}
