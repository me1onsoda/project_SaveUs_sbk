package com.example.Ex02.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ObesityRateService {

    @Autowired
    private RestTemplate restTemplate;

    public int getObesityPercent(int userId) {

        String url = "http://3.37.90.119:8001/predict-risk/" + userId;

        Map<String, Object> result =
                restTemplate.getForEntity(url, Map.class).getBody();

        double risk = 0.0;
        if (result != null && result.get("risk_score") != null) {
            risk = Double.parseDouble(result.get("risk_score").toString());
        }
        return (int) Math.round(risk); // 0~100
    }
}

