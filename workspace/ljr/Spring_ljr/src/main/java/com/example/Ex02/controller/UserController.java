package com.example.Ex02.controller;

import com.example.Ex02.dto.SurveyDto;
import com.example.Ex02.dto.UserDto;
import com.example.Ex02.dto.UserLoginDto;
import com.example.Ex02.mapper.UserMapper;
import com.example.Ex02.Service.SurveyService;

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

    // -------------------- 회원가입 --------------------
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

    // =========================================================
    // 이메일 중복 확인 (중요 — JS와 연결되는 엔드포인트)
    // =========================================================

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

    @PostMapping("/login")
    public String doLogin(
            @Valid @ModelAttribute("userLoginDto") UserLoginDto userLoginDto,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "user/userLogin";
        }

        UserDto user = userMapper.findByEmail(userLoginDto.getEmail());

        if (user == null || !user.getPassword().equals(userLoginDto.getPassword())) {
            model.addAttribute("loginError", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return "user/userLogin";
        }

        session.setAttribute("userId", user.getUserId());

        return "redirect:/survey";
    }

    // =========================================================
    // 설문
    // =========================================================

    @GetMapping("/survey")
    public String surveyPage(@ModelAttribute("surveyDto") SurveyDto surveyDto) {
        return "survey/surveyForm";
    }

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
    public String showMyPage(){
        return "user/myPage";
    }

    // 마이페이지 -> 프로필 수정 이동
    @GetMapping("/my-page/edit")
    public String editProfilePage() {
        return "user/myPageEdit";
    }


}
