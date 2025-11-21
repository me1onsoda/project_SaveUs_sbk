package com.example.Ex02.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TempLoginController {

    /**
     * 개발 테스트용 임시 로그인
     * http://localhost:8080/temp-login 으로 접속 시
     * 강제로 USER_ID = 1L (1번 사용자)로 세션에 등록하고 /community 로 이동시킵니다.
     */
    @GetMapping("/temp-login")
    public String forceLogin(HttpSession session) {
        Long testUserId = 1L;
        // UserController의 로그인 성공 로직과 동일하게 세션에 userId 저장
        session.setAttribute("userId", testUserId);

        // (선택) UserDto 전체를 저장해야 다른 기능(예: 마이페이지)이 작동한다면
        // UserMapper를 @Autowired로 주입받아 userDto를 찾아 세션에 넣어도 됩니다.
        // UserDto user = userMapper.findById(testUserId);
        // session.setAttribute("user", user);

        // 즉시 커뮤니티 페이지로 이동
        return "redirect:/community";
    }
}