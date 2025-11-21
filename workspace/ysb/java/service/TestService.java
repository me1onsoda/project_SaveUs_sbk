package com.ysb.library.service;

import com.ysb.library.dto.ResultDto;
import com.ysb.library.dto.TestDto;
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

    public TestDto get_items(MultipartFile file) throws Throwable {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        String url = "http://localhost:8000/api_test";
        ResponseEntity<ResultDto> response = restTemplate.postForEntity(url, request, ResultDto.class);
        ResultDto result = response.getBody();
        List<TestDto> items = result.getItems();

        // 확인용
        result.printItems();

        TestDto total = items.stream().reduce(
                new TestDto(
                        0F,            // calcium_mg
                        0F,            // calories_kcal (float)
                        0F,            // carbs_g
                        null,          // category
                        0F,            // fat_g
                        0F,            // fiber_g
                        0,             // food_id
                        "",          // food_name
                        0F,            // protein_g
                        0F,            // sodium_mg
                        0F             // sugar_g
                ),
                (a, b) -> new TestDto(
                        a.getCalcium_mg(),
                        a.getCalories_kcal() + b.getCalories_kcal(),
                        a.getCarbs_g() + b.getCarbs_g(),
                        null,
                        a.getFat_g() + b.getFat_g(),
                        a.getFiber_g() + b.getFiber_g(),
                        0,
                        a.getFood_name() + ", " + b.getFood_name(),
                        a.getProtein_g() + b.getProtein_g(),
                        a.getSodium_mg() + b.getSodium_mg(),
                        a.getSugar_g() + b.getSugar_g()
                )
        );


        System.out.println(total);
        // DB저장

        return total;
    }
}

