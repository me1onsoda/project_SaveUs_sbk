package com.example.Ex02.controller;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/report")
public class ReportController {

    @GetMapping
    public String showReportPage(HttpSession session, Model model) {
        // 1. 세션 확인 (로그인 여부 체크)
        // 'loginUser'는 로그인 시 세션에 저장한 키값이라고 가정합니다.
        Object loginUser = session.getAttribute("loginUser");

        if (loginUser == null) {
            // 로그인되어 있지 않다면 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }

        // 2. 사용자 정보 전달 (필요 시)
        // 추후 여기서 DB 데이터를 조회하여 model.addAttribute로 넘겨주면 됩니다.
        // 현재는 프론트엔드 목업 데이터를 사용하므로 패스합니다.
        model.addAttribute("user", loginUser);

        // 3. 뷰 페이지 반환 (templates/report.html 또는 views/report.jsp)
        return "report";
    }
}