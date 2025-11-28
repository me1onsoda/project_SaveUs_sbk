package com.example.Ex02.controller;

import com.example.Ex02.dto.ExerciseRecommendResponseDto;
import com.example.Ex02.service.ExerciseRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ExerciseRecommendController {

    private final ExerciseRecommendService exerciseRecommendService;

    @GetMapping("/exercise/recommend/{userId}")
    public String recommend(@PathVariable int userId, Model model) {

        ExerciseRecommendResponseDto result =
                exerciseRecommendService.getExerciseRecommend(userId);

        model.addAttribute("data", result);

        return "report";
    }
}
