package com.ysb.library.controller;

import com.ysb.library.dto.TestDto;
import com.ysb.library.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class TestController {

    @Autowired
    TestService testService;

    @GetMapping("/test")
    public String get_test(Model model) {
        model.addAttribute("result", "GET");
        model.addAttribute(new TestDto());
        return "common/test";
    }

    @PostMapping("/test")
    public String test(Model model,
                       TestDto testDto) {
        MultipartFile file = testDto.getFile();
        TestDto total;

        try {
            total = testService.get_items(file);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            return "redirect:/test";
        }

        model.addAttribute("result", total);

        return "common/test";
    }
}
