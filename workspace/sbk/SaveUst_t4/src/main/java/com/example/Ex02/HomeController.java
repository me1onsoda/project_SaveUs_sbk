package com.example.Ex02;

import com.example.Ex02.dto.CommentDto; // CommentDto import 추가
import com.example.Ex02.dto.PostRequestDto; // PostRequestDto import 추가
import com.example.Ex02.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*; // GetMapping, PathVariable, ResponseBody, PostMapping 임포트

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {


    private final CommunityService communityService;

    @GetMapping(value = "/")
    public String home(){
        // 중에 "dashboard" 템플릿으로
        return "layout";
    }

    @GetMapping(value = "/community")
    public String community(Model model){
        Object postList = communityService.getPostList();
        Object trendingList = communityService.getTrendingList();

        model.addAttribute("postList", postList);
        model.addAttribute("trendingList", trendingList);
        model.addAttribute("postRequestDto", new PostRequestDto()); // 새 게시물 폼을 위한

        return "community";
    }

    @PostMapping("/community/post/new")
    public String createNewPost(PostRequestDto postRequestDto) {
        communityService.createPost(postRequestDto);

        return "redirect:/community";
    }

    @GetMapping("/api/posts/{postId}/comments")
    @ResponseBody
    public List<CommentDto> getCommentsForPost(@PathVariable("postId") long postId) {
        return communityService.getCommentsByPostId(postId);
    }
}