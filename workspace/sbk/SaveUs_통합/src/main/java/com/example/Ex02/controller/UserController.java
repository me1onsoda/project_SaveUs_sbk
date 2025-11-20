package com.example.Ex02.controller;

import com.example.Ex02.dto.UserJoinDto;
import com.example.Ex02.dto.UserLoginDto;
import com.example.Ex02.mapper.SurveyMapper;
import com.example.Ex02.mapper.UserMapper;
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
import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SurveyMapper surveyMapper;

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

        userMapper.insertUser(userJoinDto);

        session.setAttribute("userId", userJoinDto.getUserId());
        session.setAttribute("loginUser", userJoinDto);

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
    public String myPage(HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        UserJoinDto user = userMapper.findById(userId);
        model.addAttribute("user", user);

        String dietType = surveyMapper.findDietType(userId);
        model.addAttribute("dietType", dietType);

        return "user/myPage";
    }

    // 프로필 수정 이동
    @GetMapping("/profile/edit")
    public String editProfilePage(@ModelAttribute("user") UserJoinDto dto,
                                  HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        UserJoinDto user = userMapper.findById(userId);
        model.addAttribute("user", user);

        return "user/profileEdit";
    }

    // 프로필 수정
    @PostMapping("/profile/edit")
    public String updateProfile(
            @ModelAttribute("user") UserJoinDto userDto,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        userDto.setUserId(userId);

        // 기존 DB 값 불러오기
        UserJoinDto originUser = userMapper.findById(userId);
        if (originUser == null) return "redirect:/login";

        try {
            // 1) 이미지 업로드한 경우
            if (profileImage != null && !profileImage.isEmpty()) {

                String uploadDir = "C:/uploads/profile/";
                File folder = new File(uploadDir);
                if (!folder.exists()) folder.mkdirs();

                String fileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
                File uploadPath = new File(uploadDir + fileName);
                profileImage.transferTo(uploadPath);

                // DB에는 웹 경로 저장
                userDto.setProfileImageUrl("/uploads/profile/" + fileName);

            } else {
                // 2) 이미지 업로드 안한 경우 → 기존 거 유지
                String oldPath = originUser.getProfileImageUrl();

                // DB에 절대경로로 저장된 적이 있다면 변환
                if (oldPath != null && oldPath.startsWith("C:/uploads")) {
                    oldPath = oldPath.replace("C:/uploads", "/uploads");
                }

                // null이면 기본 이미지
                if (oldPath == null || oldPath.isEmpty()) {
                    oldPath = "/images/icon/mypage.png";
                }

                userDto.setProfileImageUrl(oldPath);
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

        userMapper.deleteUser(userId);
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
            RedirectAttributes redirectAttributes
    ) {
        // 로그인 안 한 경우
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";



        // 새 비밀번호 제확인 불일치 => 에러메시지 html에 전달 => js로 전달 후 alert
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("pwError", "새 비밀번호가 일치하지 않습니다.");
            return "redirect:/profile/edit";
        }

        // DB에서 현재 비밀번호 체크
        int checkPw = userMapper.checkPassword(userId, currentPassword);
        if (checkPw == 0) {
            redirectAttributes.addFlashAttribute("pwError", "현재 비밀번호가 일치하지 않습니다.");
            return "redirect:/profile/edit";
        }

        // 비밀번호 변경 실행
        userMapper.updatePassword(userId, newPassword);

        // 성공 알림
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

        // 로그인 하지 않은 사용자는 로그인 페이지로 이동
        if (loginUserId == null) {
            return "redirect:/login";
        }

        // 대상 유저 정보 조회
        UserJoinDto profile = userMapper.findMainInfo(targetUserId);

        // 유저가 존재하지 않으면 404 페이지 이동
        if (profile == null) {
            return "error/404";
        }

        // 사용자 프로필 데이터 전달
        model.addAttribute("profile", profile);

        // 해당 프로필이 본인 것인지 여부
        model.addAttribute("isOwner", loginUserId.equals(targetUserId));

        // 식단 유형(설문 결과) 로드
        String dietType = surveyMapper.findDietType(targetUserId);
        model.addAttribute("dietType", dietType);

        return "user/otherProfile";  
    }

}
