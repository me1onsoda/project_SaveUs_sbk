package com.example.Ex02.controller;

import com.example.Ex02.dto.SurveyDto;
import com.example.Ex02.dto.UserDto;
import com.example.Ex02.dto.UserLoginDto;
import com.example.Ex02.mapper.UserMapper;
import com.example.Ex02.service.SurveyService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SurveyService surveyService;

    @GetMapping("/join")
    public String showJoinPage(@ModelAttribute("userDto") UserDto userDto) {
        return "user/userInsert";
    }

    @PostMapping("/user/insert")
    public String insertUser(
            @Valid @ModelAttribute("userDto") UserDto userDto,
            BindingResult bindingResult,
            HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "user/userInsert";
        }

        userMapper.insertUser(userDto);
        session.setAttribute("userId", userDto.getUserId());

        return "redirect:/survey";
    }

    // 이메일 중복 확인 (중요 — JS와 연결되는 엔드포인트)
    @GetMapping("/user/checkEmail")
    @ResponseBody
    public String checkEmail(@RequestParam("email") String email) {

        int count = userMapper.countByEmail(email);

        return (count > 0) ? "duplicate" : "ok";
    }

    // 로그인
    @GetMapping("/login")
    public String showLoginPage(@ModelAttribute("userLoginDto") UserLoginDto userLoginDto) {
        return "user/userLogin";
    }

    @PostMapping("/login/proc")
    public String doLogin(
            @Valid @ModelAttribute("userLoginDto") UserLoginDto userLoginDto,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "user/userLogin";
        }

        // 이메일로 사용자 조회
        UserDto user = userMapper.findByEmail(userLoginDto.getEmail());

        // 사용자 없음 또는 비밀번호 불일치
        if (user == null || !user.getPassword().equals(userLoginDto.getPassword())) {
            model.addAttribute("loginError", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return "user/userLogin";
        }

        // 로그인 성공 → 세션 등록
        session.setAttribute("user", user);        // 사용자 전체 저장
        session.setAttribute("userId", user.getUserId());  // 기본 키 저장

        return "redirect:/dashboard";
    }

    // 회원가입 후 설문지페이지
    @GetMapping("/survey")
    public String surveyPage(@ModelAttribute("surveyDto") SurveyDto surveyDto) {
        return "survey/surveyForm";
    }

    // 설문지 입력 후 처리
    @PostMapping("/survey/submit")
    public String submitSurvey(
            @Valid @ModelAttribute("surveyDto") SurveyDto surveyDto,
            BindingResult bindingResult,
            HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "survey/surveyForm";
        }

        Long userId = (Long) session.getAttribute("userId");
        surveyService.processSurvey(surveyDto, userId);

        return "redirect:/challenge/recommend";
    }

    // 마이페이지
    @GetMapping("/my-page")
    public String showMyPage(HttpSession session, Model model){

        // 세션에서 로그인 사용자 ID 얻기
        Long userId = (Long) session.getAttribute("userId");

        // 로그인 상태 아니면 로그인 페이지로
        if (userId == null) {
            return "redirect:/login";
        }

        // DB에서 사용자 정보 조회
        UserDto user = userMapper.findById(userId);
        model.addAttribute("user", user);

        return "user/myPage";
    }

    // 마이페이지 -> 프로필 수정 이동
    @GetMapping("/my-page/edit")
    public String editProfilePage() {
        return "user/myPageEdit";
    }


}
