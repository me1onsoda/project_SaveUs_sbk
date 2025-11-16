package com.example.Ex02.controller;

import com.example.Ex02.dto.SurveyDto;
<<<<<<< HEAD
import com.example.Ex02.dto.UserDto;
import com.example.Ex02.dto.UserLoginDto;
import com.example.Ex02.mapper.UserMapper;
import com.example.Ex02.Service.SurveyService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

=======
import com.example.Ex02.dto.UserJoinDto;
import com.example.Ex02.dto.UserLoginDto;
import com.example.Ex02.mapper.SurveyMapper;
import com.example.Ex02.mapper.UserMapper;
import com.example.Ex02.service.SurveyService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
>>>>>>> home2
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
<<<<<<< HEAD
=======
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
>>>>>>> home2

@Controller
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
<<<<<<< HEAD
    private SurveyService surveyService;

    // -------------------- 회원가입 --------------------
    @GetMapping("/join")
    public String showJoinPage(@ModelAttribute("userDto") UserDto userDto) {
        return "user/userInsert";
    }

    @PostMapping("/user/insert")
    public String insertUser(
            @Valid @ModelAttribute("userDto") UserDto userDto,
=======
    private SurveyMapper surveyMapper;

    @Autowired
    private SurveyService surveyService;

    // 회원가입 페이지 이동
    @GetMapping("/join")
    public String joinPage(@ModelAttribute("userDto") UserJoinDto userJoinDto) {
        return "user/userInsert";
    }

    // 회원가입 처리
    @PostMapping("/user/insert")
    public String insertUser(
            @Valid @ModelAttribute("userDto") UserJoinDto userJoinDto,
>>>>>>> home2
            BindingResult bindingResult,
            HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "user/userInsert";
        }

<<<<<<< HEAD
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
=======
        // DB에 INSERT (userId는 MyBatis에서 keyProperty로 채워져야 함)
        userMapper.insertUser(userJoinDto);

        // userId가 정상적으로 들어왔는지 확인
        System.out.println("회원가입 후 생성된 userId = " + userJoinDto.getUserId());

        // 세션 저장
        session.setAttribute("userId", userJoinDto.getUserId());
        session.setAttribute("loginUser", userJoinDto);

        return "redirect:/survey"; // 필요하면 survey로 변경
    }

    // 이메일 중복확인
    @GetMapping("/user/checkEmail")
    @ResponseBody
    public String checkEmail(@RequestParam("email") String email) {
        int cnt = userMapper.countByEmail(email);
        return cnt > 0 ? "duplicate" : "ok";
    }

    // 설문 페이지
    @GetMapping("/survey")
    public String surveyPage(@ModelAttribute("surveyDto")SurveyDto surveyDto, HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");

        // 회원가입/로그인 안 했으면 접근 불가
        if (userId == null) {
            return "redirect:/login";
        }
        return "survey/surveyForm";   // templates/survey/surveyForm.html
    }

    @PostMapping("/survey/submit")
    public String submitSurvey(@Valid @ModelAttribute("surveyDto") SurveyDto surveyDto,
                               BindingResult bindingResult,
                               HttpSession session,
                               Model model) {

        Long userId = (Long) session.getAttribute("userId");

        // 비정상 접근 방지
>>>>>>> home2
        if (userId == null) {
            return "redirect:/login";
        }

<<<<<<< HEAD
        // DB에서 사용자 정보 조회
        UserDto user = userMapper.findById(userId);
=======
        // 문항 체크 안 했을 때
        if (bindingResult.hasErrors()) {
            return "survey/surveyForm";
        }

        // 설문지 점수계산
        surveyService.evaluateSurvey(surveyDto);
        // 결과 페이지로 전달
        model.addAttribute("result", surveyDto);

        return "survey/surveyResult";
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("userLoginDto", new UserLoginDto());
        return "user/userLogin";
    }

    // 로그인 처리
    @PostMapping("/login/proc")
    public String doLogin(
            @ModelAttribute("userLoginDto") UserLoginDto userLoginDto,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 로그인 DTO의 이메일로 조회
        UserJoinDto user = userMapper.findByEmail(userLoginDto.getEmail());
        System.out.println("조회된 유저: " + user);

        // 아이디 없음
        if (user == null) {
            redirectAttributes.addFlashAttribute("loginError", "이메일 또는 비밀번호가 일치하지 않습니다.");
            return "redirect:/login";
        }

        // 비밀번호 불일치
        if (!user.getPassword().equals(userLoginDto.getPassword())) {
            redirectAttributes.addFlashAttribute("loginError", "이메일 또는 비밀번호가 일치하지 않습니다.");
            return "redirect:/login";
        }

        // 로그인 성공 > 세션 저장
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("loginUser", user);

        return "redirect:/my-page";
    }

    // 마이페이지
    @GetMapping("/my-page")
    public String myPage(HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");

        // 로그인 안 되어 있을 때
        if (userId == null) {
            return "redirect:/login";
        }

        // 사용자 정보 조회
        UserJoinDto user = userMapper.findById(userId);
>>>>>>> home2
        model.addAttribute("user", user);

        return "user/myPage";
    }
<<<<<<< HEAD

    // 마이페이지 -> 프로필 수정 이동
    @GetMapping("/my-page/edit")
    public String editProfilePage() {
        return "user/myPageEdit";
    }


=======
>>>>>>> home2
}
