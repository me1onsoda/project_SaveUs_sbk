package com.example.Ex02.controller;

import com.example.Ex02.service.HealthScoreService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @Autowired
    private HealthScoreService healthScoreService;

    @GetMapping("/score/calc")
    public String calcScores(HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        // 로그인 안 되어 있을 때 방어 코드
        if (userId == null) {
            return "redirect:/login";
        }

        // 점수 계산 수행
        healthScoreService.refreshDailyScores(userId);

        return "redirect:/calendar";
    }

}
