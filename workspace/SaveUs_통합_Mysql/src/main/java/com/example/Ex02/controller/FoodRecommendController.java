package com.example.Ex02.controller;

import com.example.Ex02.dto.FoodRecommendRequest;
import com.example.Ex02.dto.FoodRecommendResponse;
import com.example.Ex02.service.FoodRecommendService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/food")
public class FoodRecommendController {

    private final FoodRecommendService foodRecommendService;

    public FoodRecommendController(FoodRecommendService foodRecommendService) {
        this.foodRecommendService = foodRecommendService;
    }

    @GetMapping("/recommend/{userId}")
    public FoodRecommendResponse recommend(@PathVariable int userId) {

        FoodRecommendRequest req = new FoodRecommendRequest();
        req.setUser_id(userId);

        return foodRecommendService.getFoodRecommend(req);
    }
}
