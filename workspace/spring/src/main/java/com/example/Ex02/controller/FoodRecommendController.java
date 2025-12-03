package com.example.Ex02.controller;

import com.example.Ex02.dto.FoodRecommendRequest;
import com.example.Ex02.dto.FoodRecommendResponse;
import com.example.Ex02.service.FoodRecommendService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/foodRecommend")
public class FoodRecommendController {

    private final FoodRecommendService recommendService;

    public FoodRecommendController(FoodRecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @PostMapping
    public FoodRecommendResponse recommend(@RequestBody FoodRecommendRequest req) {
        return recommendService.getFoodRecommend(req);
    }
}
