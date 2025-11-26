package com.example.Ex02.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PolicyController {

    @GetMapping("/terms")
    public String terms() {
        return "common/terms";
    }

    @GetMapping("/privacy")
    public String privacy(){
        return "common/privacy";
    }
}
