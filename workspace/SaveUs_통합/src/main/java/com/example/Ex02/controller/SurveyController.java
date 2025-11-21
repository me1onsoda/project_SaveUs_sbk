package com.example.Ex02.controller;

import com.example.Ex02.dto.SurveyDto;
import com.example.Ex02.dto.UserJoinDto;
import com.example.Ex02.mapper.SurveyMapper;
import com.example.Ex02.mapper.UserMapper;
import com.example.Ex02.service.SurveyService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SurveyMapper surveyMapper;

    // 설문 페이지
    @GetMapping("/survey")
    public String surveyPage(@ModelAttribute("surveyDto") SurveyDto surveyDto,
                             HttpSession session,
                             Model model) {

        Long userId = (Long) session.getAttribute("userId");
        UserJoinDto tempUser = (UserJoinDto) session.getAttribute("tempUser");

        // 로그인 상태 또는 회원가입 후 tempUser 존재 시 접근 허용
        if (userId == null && tempUser == null) {
            return "redirect:/login";
        }

        return "survey/surveyForm";
    }

    // 설문 제출
    @PostMapping("/survey/submit")
    public String submitSurvey(
            @Valid @ModelAttribute("surveyDto") SurveyDto surveyDto,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute("userId");
        UserJoinDto tempUser = (UserJoinDto) session.getAttribute("tempUser");

        if (userId == null && tempUser == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            return "survey/surveyForm";
        }

        surveyService.evaluateSurvey(surveyDto);
        String dietType = surveyService.getDietType(surveyDto);

        // 회원가입 직후 설문 제출 시 DB에 사용자 저장
        if (userId == null && tempUser != null) {

            userMapper.insertUser(tempUser);

            UserJoinDto savedUser = userMapper.findByEmail(tempUser.getEmail());

            session.setAttribute("userId", savedUser.getUserId());
            session.removeAttribute("tempUser");

            userId = savedUser.getUserId();
        }

        UserJoinDto user = userMapper.findById(userId);

        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("result", surveyDto);
        model.addAttribute("dietType", dietType);

        surveyMapper.saveSurveyResult(userId, surveyDto, dietType);

        return "survey/surveyResult";
    }
}
