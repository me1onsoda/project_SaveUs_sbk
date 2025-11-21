package com.example.Ex02.controller;

import com.example.Ex02.dto.UserJoinDto;
import com.example.Ex02.mapper.UserMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserFindController {

    @Autowired
    private UserMapper userMapper;

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
    @PostMapping("/user/find-id")
    public String findId(
            @RequestParam("nickname") String nickname,
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("day") String day,
            Model model) {

        year = year.replaceAll("[^0-9]", "");
        month = month.replaceAll("[^0-9]", "");
        day = day.replaceAll("[^0-9]", "");

        if (month.length() == 1) month = "0" + month;
        if (day.length() == 1) day = "0" + day;

        String birthdate = String.format("%s-%s-%s", year, month, day);

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

        // 실패
        if (user == null || !user.getNickname().equals(nickname)) {
            model.addAttribute("findError", "일치하는 회원 정보를 찾을 수 없습니다.");
            return "user/findPw";
        }

        // 성공 → 재설정 대상 저장
        session.setAttribute("resetUserId", user.getUserId());

        // 안내 메시지
        model.addAttribute("findSuccess", "회원 정보가 확인되었습니다. 비밀번호를 재설정하세요.");

        // 비밀번호 재설정 페이지로 이동
        return "redirect:/user/reset-pw";
    }

    // 비밀번호 재설정 페이지
    @GetMapping("/user/reset-pw")
    public String resetPwPage() {
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
