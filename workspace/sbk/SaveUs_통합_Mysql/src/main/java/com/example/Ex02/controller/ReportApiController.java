package com.example.Ex02.controller;

import com.example.Ex02.dto.ReportDto;
import com.example.Ex02.service.ReportService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportApiController {

    private final ReportService reportService;

    @GetMapping("/daily")
    public ResponseEntity<ReportDto> getDailyReport(
            @RequestParam(value = "date", required = false) String date,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).build();

        if (date == null || date.isEmpty()) {
            date = LocalDate.now().toString();
        }

        ReportDto data = reportService.getReportData(userId, date);
        return ResponseEntity.ok(data);
    }
}