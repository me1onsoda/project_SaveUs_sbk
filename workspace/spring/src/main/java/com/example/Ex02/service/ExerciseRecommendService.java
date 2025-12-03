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

        String url = "http://3.37.90.119:8003/recommend/" + userId;



        return restTemplate.getForObject(url, ExerciseRecommendResponseDto.class);
    }
}
