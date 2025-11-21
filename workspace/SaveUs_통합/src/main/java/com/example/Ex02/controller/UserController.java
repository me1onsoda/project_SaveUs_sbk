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
    public String loginForm(Model model,  @RequestParam(value = "resetSuccess", required = false) String resetSuccess) {
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

        // 유저 식단 유형 조회 (Balanced, HC_HIGH, 등등)
        String dietType = surveyMapper.findDietType(userId);
        model.addAttribute("dietType", dietType);

        // 사용자 정보
        UserJoinDto user = userMapper.findById(userId);
        model.addAttribute("user", user);

        // 획득 뱃지 목록
        List<UserBadgeDto> badges = userBadgeMapper.findBadgesByUser(userId);
        model.addAttribute("badges", badges);

        // 획득 챌린지 목록
        List<UserChallengeDto> challenges  = userChallengeMapper.findActiveChallenges(userId);
        model.addAttribute("challenges", challenges);

        return "user/myPage";
    }


    @GetMapping("/profile/edit")
    public String editPage(HttpSession session, Model model) {
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
        if (loginUserId == null) return "redirect:/login";

        UserJoinDto profile = userMapper.findMainInfo(targetUserId);
        if (profile == null) return "error/404";

        // 획득 뱃지 목록 (targetUserId 기준)
        List<UserBadgeDto> badges = userBadgeMapper.findBadgesByUser(targetUserId);
        model.addAttribute("badges", badges);

        // 진행중인 챌린지 목록 (targetUserId 기준)
        List<UserChallengeDto> challenges = userChallengeMapper.findActiveChallenges(targetUserId);
        model.addAttribute("challenges", challenges);


        model.addAttribute("profile", profile);
        model.addAttribute("isOwner", loginUserId.equals(targetUserId));

        return "user/otherProfile";
    }

}
