package com.example.Ex02.controller;

import com.example.Ex02.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

        return "community";
    }

}