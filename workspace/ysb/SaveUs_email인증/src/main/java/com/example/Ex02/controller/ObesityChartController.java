package com.example.Ex02.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Controller
public class ObesityChartController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/user/obesity-chart")
    public String showObesityChart(
            @RequestParam(name = "userId", required = false, defaultValue = "1") Integer userId,
            Model model) {

        String url = "http://3.37.90.119:8001/predict-obesity/" + userId;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> result = response.getBody();

        double probability = 0.0;
        if (result != null && result.get("probability") != null) {
            probability = Double.parseDouble(result.get("probability").toString()) * 100;
        }

        model.addAttribute("userId", userId);
        model.addAttribute("obesityProbability", probability);

        return "chart/obesity-chart";
    }
}
