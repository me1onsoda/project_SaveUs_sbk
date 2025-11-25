package com.example.Ex02.controller;

import com.example.Ex02.dto.CalendarDayDto;
import com.example.Ex02.service.CalendarService;
import com.example.Ex02.service.HealthScoreService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private HealthScoreService healthScoreService;  // 건강점수 자동 계산 서비스

    /**
     * 건강점수 캘린더 페이지
     */
    @GetMapping("/calendar")
    public String calendarPage(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            HttpSession session,
            Model model
    ) {

        // 세션에서 유저 ID 조회
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        LocalDate today = LocalDate.now();

        // 연/월 기본값 설정 (요청값이 없으면 현재 날짜 기준)
        int y = (year == null) ? today.getYear() : year;
        int m = (month == null) ? today.getMonthValue() : month;

        // 월 범위 보정
        if (m < 1) {
            m = 12;
            y -= 1;
        } else if (m > 12) {
            m = 1;
            y += 1;
        }

        // 선택된 월의 건강점수 자동 계산 수행
        healthScoreService.calculateMonthlyScore(userId, y, m);

        // 선택된 월의 달력(하루 단위 데이터) 조회
        List<CalendarDayDto> days = calendarService.getMonthlyCalendar(userId, y, m);

        model.addAttribute("days", days);
        model.addAttribute("year", y);
        model.addAttribute("month", m);
        model.addAttribute("today", today);

        return "calendar";
    }
}
