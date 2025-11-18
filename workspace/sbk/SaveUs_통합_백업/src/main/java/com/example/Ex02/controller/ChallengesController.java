package com.example.Ex02.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChallengesController {
    @GetMapping("/challenges")
    public String challenges(){
        return "challenges";
    }
}
