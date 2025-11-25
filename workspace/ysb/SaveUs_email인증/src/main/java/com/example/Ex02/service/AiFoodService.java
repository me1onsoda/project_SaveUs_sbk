package com.example.Ex02.service;

import com.example.Ex02.dto.AiDto;
import com.example.Ex02.dto.AiResponseWrapper;
import com.example.Ex02.dto.DiabetesScoreDto;
import com.example.Ex02.dto.MealSaveDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiFoodService {

    private final RestTemplate restTemplate = new RestTemplate();
    // 파이썬 서버 주소 확인 필요 (localhost:8000 인지 등)
    private final String FASTAPI_URL = "http://127.0.0.1:8000/api_test";


    // ★ 반환 타입을 List<MealSaveDto>로 변경 ★
    public List<MealSaveDto> analyzeImage(MultipartFile file) {
        List<MealSaveDto> resultList = new ArrayList<>(); // 결과를 담을 리스트

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<AiResponseWrapper> response = restTemplate.postForEntity(
                    FASTAPI_URL, requestEntity, AiResponseWrapper.class
            );

            // ★ for문으로 모든 음식 변환 ★
            if (response.getBody() != null && !response.getBody().getItems().isEmpty()) {

                for (AiDto aiData : response.getBody().getItems()) {
                    MealSaveDto myDto = new MealSaveDto();
                    myDto.setMealName(aiData.getFoodName());
                    myDto.setCalories((int) Math.round(aiData.getCalories()));
                    myDto.setCarbs((int) Math.round(aiData.getCarbs()));
                    myDto.setProtein((int) Math.round(aiData.getProtein()));
                    myDto.setFat((int) Math.round(aiData.getFat()));
                    myDto.setSugar((int) Math.round(aiData.getSugar()));
                    myDto.setFiber((int) Math.round(aiData.getFiber()));
                    myDto.setCalcium((int) Math.round(aiData.getCalcium()));
                    myDto.setSodium((int) Math.round(aiData.getSodium()));

                    resultList.add(myDto); // 리스트에 추가
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList; // 리스트 반환 (비어있을 수도 있음)
    }

    // 파이썬 통신 메서드 추가
    public List<DiabetesScoreDto> calculateDietScores(List<DiabetesScoreDto> requestList) {
        if (requestList.isEmpty()) return new ArrayList<>();

        String pythonUrl = "http://3.37.90.119:8000/api/calculate-score";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<List<DiabetesScoreDto>> requestEntity = new HttpEntity<>(requestList, headers);

            ResponseEntity<List<DiabetesScoreDto>> response = restTemplate.exchange(
                    pythonUrl, HttpMethod.POST, requestEntity,
                    new ParameterizedTypeReference<List<DiabetesScoreDto>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}