package com.example.Ex02.controller;
import com.example.Ex02.dto.PostRequestDto;
import com.example.Ex02.service.CommunityService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping(value = "/community")
    public String community(Model model, HttpSession session){

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            userId = 0L; // 비로그인 사용자는 0L로 처리 (게시물 좋아요 상태 확인용)
        }

        // 서비스 호출
        Object postList = communityService.getPostList(userId);
        Object trendingList = communityService.getTrendingList();

        // 모델에 데이터 추가
        model.addAttribute("postList", postList);
        model.addAttribute("trendingList", trendingList);
        model.addAttribute("postRequestDto", new PostRequestDto()); // 폼을 위한 빈 객체
        model.addAttribute("currentUserId", userId); // JS에서 사용할 현재 유저 ID

        return "community";
    }

    @PostMapping("/community/post/new")
    public String createNewPost(
            @ModelAttribute PostRequestDto postRequestDto,
            HttpSession session) throws IOException {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login"; // 비로그인 시 작성 불가, 로그인 페이지로
        }

        communityService.createPost(postRequestDto, userId);

        return "redirect:/community"; // 작성 완료 후 커뮤니티 페이지로 리다이렉트
    }
}