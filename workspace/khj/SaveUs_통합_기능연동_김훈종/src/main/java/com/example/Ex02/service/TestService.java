package com.example.Ex02.service;


import com.example.Ex02.dto.ResultDto;
import com.example.Ex02.dto.TestDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class TestService {
    private final RestTemplate restTemplate = new RestTemplate();

    // Controller에서 페이지로 데이터를 줄 거면 TestDto(실제 DTO로 변경), 아니라면 void
    public TestDto get_items(MultipartFile file) throws Throwable {
        
        // fastapi POST 요청을 위한 header 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // body 구성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        // request 객체 생성
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        // endpoint 이름 변경 예정(api_test 부분)
        String url = "http://localhost:8000/api_test";

        // POST 요청 후, resultDto Entity로 response를 받아옴
        ResponseEntity<ResultDto> response = restTemplate.postForEntity(url, request, ResultDto.class);
        ResultDto result = response.getBody();

        // response에서 items만 가져오기
        List<TestDto> items = result.getItems();

        // 확인용
        result.printItems();

        // 받아온 TestDto List를 해당 식사의 합계로 계산
        // foodName은 ", "로 이어주고(초기 값으로 ""가 들어가서 ', ' 붙어있는데 제거 필요
        // 나머지 영양소의 합계를 계산하여 TestDto total에 할당
        TestDto total = items.stream().reduce(
                new TestDto(
                        0,            // calcium_mg
                        0,            // calories_kcal (float)
                        0,            // carbs_g
                        null,          // category
                        0,            // fat_g
                        0,            // fiber_g
                        0,             // food_id
                        "",          // food_name
                        0,            // protein_g
                        0,            // sodium_mg
                        0             // sugar_g
                ),
                (a, b) -> new TestDto(
                        a.getCalciumMg(),
                        a.getCaloriesKcal() + b.getCaloriesKcal(),
                        a.getCarbsG() + b.getCarbsG(),
                        null,
                        a.getFatG() + b.getFatG(),
                        a.getFiberG() + b.getFiberG(),
                        0,
                        a.getFoodName() + ", " + b.getFoodName(),
                        a.getProteinG() + b.getProteinG(),
                        a.getSodiumMg() + b.getSodiumMg(),
                        a.getSugarG() + b.getSugarG()
                )
        );

        System.out.println(total);

        // 이하 DB저장 및 로직 구현





        // Controller에서 페이지로 데이터를 줄 거면 return total
        return total;
    }
}

