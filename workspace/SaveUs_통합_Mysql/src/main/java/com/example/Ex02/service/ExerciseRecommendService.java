package com.example.Ex02.service;

import com.example.Ex02.dto.ExerciseRecommendResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ExerciseRecommendService {

    private final RestTemplate restTemplate = new RestTemplate();

    public ExerciseRecommendResponseDto getExerciseRecommend(int userId) {

        // FastAPI 서버 URL
        String url = "http://3.37.90.119:8003/recommend/" + userId;

        // FastAPI에서 JSON을 받아 DTO로 매핑
        ExerciseRecommendResponseDto response =
                restTemplate.getForObject(url, ExerciseRecommendResponseDto.class);

        return response;
    }
}
