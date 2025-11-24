package com.example.Ex02.controller;

import com.example.Ex02.dto.*;
import com.example.Ex02.mapper.SurveyMapper;
import com.example.Ex02.mapper.UserBadgeMapper;
import com.example.Ex02.mapper.UserChallengeMapper;
import com.example.Ex02.mapper.UserMapper;
import com.example.Ex02.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SurveyMapper surveyMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserBadgeMapper userBadgeMapper;

    @Autowired
    private UserChallengeMapper userChallengeMapper;

    // 회원가입 페이지 이동
    @GetMapping("/join")
    public String joinPage(@ModelAttribute("userDto") UserJoinDto userJoinDto) {
        return "user/userInsert";
    }

    // 회원가입 처리
    @PostMapping("/user/insert")
    public String insertUser(
            @Valid @ModelAttribute("userDto") UserJoinDto userJoinDto,
            BindingResult bindingResult,
            HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "user/userInsert";
        }

        session.setAttribute("tempUser", userJoinDto);
        return "redirect:/survey";
    }

    // 이메일 중복확인
    @GetMapping("/user/checkEmail")
    @ResponseBody
    public String checkEmail(@RequestParam("email") String email) {
        return userMapper.countByEmail(email) > 0 ? "duplicate" : "ok";
    }

    // 닉네임 중복확인
    @GetMapping("/user/checkNickname")
    @ResponseBody
    public String checkNickname(@RequestParam("nickname") String nickname) {
        return userMapper.countByNickname(nickname) > 0 ? "duplicate" : "ok";
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String loginForm(Model model, @RequestParam(value = "resetSuccess", required = false) String resetSuccess) {
        model.addAttribute("userLoginDto", new UserLoginDto());

        if (resetSuccess != null) {
            model.addAttribute("resetSuccess", true);
        }

        return "user/userLogin";
    }

    // 로그인 처리
    @PostMapping("/login/proc")
    public String doLogin(
            @ModelAttribute("userLoginDto") UserLoginDto userLoginDto,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UserJoinDto user = userMapper.findByEmail(userLoginDto.getEmail());

        if (user == null || !user.getPassword().equals(userLoginDto.getPassword())) {
            redirectAttributes.addFlashAttribute("loginError", "이메일 또는 비밀번호가 일치하지 않습니다.");
            return "redirect:/login";
        }

        session.setAttribute("userId", user.getUserId());
        session.setAttribute("loginUser", user);

        return "redirect:/dashboard";
    }

    // 마이페이지
    @GetMapping("/my-page")
    public String myPage(HttpSession session, Model model, @ModelAttribute("surveyDto") SurveyDto surveyDto) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        String dietType = surveyMapper.findDietType(userId);
        model.addAttribute("dietType", dietType);

        UserJoinDto user = userMapper.findById(userId);
        model.addAttribute("user", user);

        List<UserBadgeDto> badges = userBadgeMapper.findBadgesByUser(userId);
        model.addAttribute("badges", badges);

        List<UserChallengeDto> challenges = userChallengeMapper.findActiveChallenges(userId);
        model.addAttribute("challenges", challenges);

        return "user/myPage";
    }

    // 프로필 수정 페이지
    @GetMapping("/profile/edit")
    public String editPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        UserJoinDto user = userMapper.findById(userId);
        model.addAttribute("user", user);

        return "user/profileEdit";
    }

    // 프로필 수정 처리
    @PostMapping("/profile/edit")
    public String updateProfile(
            @ModelAttribute("user") UserJoinDto userDto,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        userDto.setUserId(userId);

        UserJoinDto originUser = userMapper.findById(userId);
        if (originUser == null) return "redirect:/login";

        try {
            String uploadDir = "/home/ubuntu/uploads/profile/";
            File folder = new File(uploadDir);
            if (!folder.exists()) folder.mkdirs();

            if (profileImage != null && !profileImage.isEmpty()) {

                String fileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
                File uploadPath = new File(uploadDir + fileName);
                profileImage.transferTo(uploadPath);

                // 새 이미지 URL 저장
                userDto.setProfileImageUrl("/uploads/profile/" + fileName);

            } else {

                // 업로드 없으면 기존 이미지 그대로 유지
                userDto.setProfileImageUrl(originUser.getProfileImageUrl());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        userMapper.updateUser(userDto);
        return "redirect:/my-page";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // 회원 탈퇴
    @PostMapping("/user/delete")
    public String deleteUser(HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        userService.deleteUserAll(userId);
        session.invalidate();

        return "redirect:/login";
    }

    // 비밀번호 변경
    @PostMapping("/profile/password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("pwError", "새 비밀번호가 일치하지 않습니다.");
            return "redirect:/profile/edit";
        }

        int checkPw = userMapper.checkPassword(userId, currentPassword);
        if (checkPw == 0) {
            redirectAttributes.addFlashAttribute("pwError", "현재 비밀번호가 일치하지 않습니다.");
            return "redirect:/profile/edit";
        }

        userMapper.updatePassword(userId, newPassword);

        redirectAttributes.addFlashAttribute("pwSuccess", "비밀번호가 성공적으로 변경되었습니다.");
        return "redirect:/profile/edit";
    }

    // 다른 사람의 프로필 조회
    @GetMapping("/user/profile/{targetUserId}")
    public String viewOtherProfile(
            @PathVariable("targetUserId") Long targetUserId,
            HttpSession session,
            Model model) {

        Long loginUserId = (Long) session.getAttribute("userId");
        if (loginUserId == null) return "redirect:/login";

        UserJoinDto profile = userMapper.findMainInfo(targetUserId);
        if (profile == null) return "error/404";

        List<UserBadgeDto> badges = userBadgeMapper.findBadgesByUser(targetUserId);
        model.addAttribute("badges", badges);

        List<UserChallengeDto> challenges = userChallengeMapper.findActiveChallenges(targetUserId);
        model.addAttribute("challenges", challenges);

        model.addAttribute("profile", profile);
        model.addAttribute("isOwner", loginUserId.equals(targetUserId));

        return "user/otherProfile";
    }
}
