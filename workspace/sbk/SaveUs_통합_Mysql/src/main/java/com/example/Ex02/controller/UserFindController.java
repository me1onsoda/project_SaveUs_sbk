package com.example.Ex02.controller;

import com.example.Ex02.dto.UserJoinDto;
import com.example.Ex02.mapper.UserMapper;
import com.example.Ex02.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserFindController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    // 선택 화면
    @GetMapping("/user/find-main")
    public String findMain() {
        return "user/findMain";
    }

    // 아이디 찾기 페이지
    @GetMapping("/user/find-id")
    public String findIdPage() {
        return "user/findId";
    }

    // 아이디 찾기 처리
    @PostMapping("/user/find-id/proc")
    public String findId(
            @RequestParam("nickname") String nickname,
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("day") String day,
            Model model) {

        // 숫자만 남기기
        year = year.replaceAll("[^0-9]", "");
        month = month.replaceAll("[^0-9]", "");
        day = day.replaceAll("[^0-9]", "");

        // 1) 값 존재 체크
        if (year.isEmpty() || month.isEmpty() || day.isEmpty()) {
            model.addAttribute("findError", "생년월일을 모두 입력해주세요.");
            return "user/findId";
        }

        // 2) 숫자로 변환
        int y, m, d;
        try {
            y = Integer.parseInt(year);
            m = Integer.parseInt(month);
            d = Integer.parseInt(day);
        } catch (Exception e) {
            model.addAttribute("findError", "생년월일은 숫자만 입력 가능합니다.");
            return "user/findId";
        }

        // 3) 범위 검증
        int currentYear = java.time.LocalDate.now().getYear();

        if (y < 1900 || y > currentYear) {
            model.addAttribute("findError", "올바른 연도를 입력해주세요.");
            return "user/findId";
        }
        if (m < 1 || m > 12) {
            model.addAttribute("findError", "월은 1~12 사이여야 합니다.");
            return "user/findId";
        }
        if (d < 1 || d > 31) {
            model.addAttribute("findError", "일은 1~31 사이여야 합니다.");
            return "user/findId";
        }

        // 4) 실제 존재하는 날짜인지 검증
        try {
            java.time.LocalDate.of(y, m, d);
        } catch (Exception e) {
            model.addAttribute("findError", "존재하지 않는 날짜입니다.");
            return "user/findId";
        }

        // 포매팅
        String birthdate = String.format("%04d-%02d-%02d", y, m, d);

        // DB 조회
        UserJoinDto user = userMapper.findByNicknameAndBirthdate(nickname, birthdate);

        if (user == null) {
            model.addAttribute("findError", "일치하는 회원 정보를 찾을 수 없습니다.");
        } else {
            model.addAttribute("foundEmail", user.getEmail());
        }

        return "user/findId";
    }


    // 비밀번호 찾기 페이지
    @GetMapping("/user/find-pw")
    public String findPwPage() {
        return "user/findPw";
    }

    // 비밀번호 찾기 처리
    @PostMapping("/user/find-pw")
    public String findPw(
            @RequestParam("email") String email,
            @RequestParam("nickname") String nickname,
            Model model,
            HttpSession session) {

        UserJoinDto user = userMapper.findByEmail(email);

        if (user == null || !user.getNickname().equals(nickname)) {
            model.addAttribute("findError", "일치하는 회원 정보를 찾을 수 없습니다.");
            return "user/findPw";
        }

        userService.sendPasswordResetMail(user);

        session.setAttribute("resetUserId", user.getUserId());
        model.addAttribute("findSuccess",
                "입력하신 이메일로 비밀번호 재설정 링크를 발송했습니다.\n메일을 확인해 주세요.");

        return "user/findPw";
    }

    // 비밀번호 재설정 페이지
    @GetMapping("/user/reset-pw/proc")
    public String resetPwPage(
            @RequestParam(value = "token", required = false) String token,
            HttpSession session,
            Model model) {

        System.out.println(token);


        if (token == null || token.isBlank()) {
            model.addAttribute("resetError", "잘못된 접근입니다.");
            return "redirect:/login";
        }

        Long userId = userService.consumePasswordResetMail(token);

        if (userId == null) {
            model.addAttribute("resetError",
                    "비밀번호 재설정 링크가 만료되었거나 올바르지 않습니다.");
            return "redirect:/login";
        }

        session.setAttribute("resetUserId", userId);
        return "user/resetPw";
    }

    // 비밀번호 재설정 처리
    @PostMapping("/user/reset-pw")
    public String resetPw(
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute("resetUserId");

        if (userId == null) {
            model.addAttribute("resetError", "인증 정보가 만료되었습니다.");
            return "user/resetPw";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("resetError", "새 비밀번호가 일치하지 않습니다.");
            return "user/resetPw";
        }

        // 비밀번호 변경
        userMapper.updatePassword(userId, newPassword);

        // 세션에서 삭제 (보안)
        session.removeAttribute("resetUserId");

        // 성공 후 로그인 페이지로 이동
        return "redirect:/login?resetSuccess=true";
    }

}
