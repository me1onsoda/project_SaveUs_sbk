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
    public String surveyPage(@ModelAttribute("surveyDto") SurveyDto surveyDto, HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");

        // 회원가입/로그인 안 했으면 접근 불가
        if (userId == null) {
            return "redirect:/login";
        }
        return "survey/surveyForm";   // templates/survey/surveyForm.html
    }

    // 설문지 결과
    @PostMapping("/survey/submit")
    public String submitSurvey(@Valid @ModelAttribute("surveyDto") SurveyDto surveyDto,
                               BindingResult bindingResult,
                               HttpSession session,
                               Model model) {

        Long userId = (Long) session.getAttribute("userId");

        // 비정상 접근 방지
        if (userId == null) {
            return "redirect:/login";
        }

        // 문항 체크 안 했을 때
        if (bindingResult.hasErrors()) {
            return "survey/surveyForm";
        }

        // 설문지 점수계산
        surveyService.evaluateSurvey(surveyDto);
        // 결과 페이지로 전달
        UserJoinDto user = userMapper.findById(userId);
        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("result", surveyDto);

        String dietType = surveyService.getDietType(surveyDto);
        session.setAttribute("dietType", dietType);
        model.addAttribute("dietType", dietType);

        surveyMapper.saveSurveyResult(userId, surveyDto, dietType);


        return "survey/surveyResult";
    }
}
