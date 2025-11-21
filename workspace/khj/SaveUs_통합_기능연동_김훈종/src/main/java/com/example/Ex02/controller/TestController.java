package com.example.Ex02.controller;


import com.example.Ex02.dto.TestDto;
import com.example.Ex02.service.TestService;
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

    // MultipartFile POST 요청을 이쪽으로 연결
    @PostMapping("/test")
    public String test(Model model,
                       TestDto testDto) {
        // TestDto가 아니더라도 MultipartFile을 가져오면 ok
        MultipartFile file = testDto.getFile();

        // 검출 결과를 받아와야 한다면 필요함(getItems가 void라면 제거)
        TestDto total;


        try {
        // total에 검출 결과(검출된 음식의 총합을 TestDto에 할당한 것)을 받아옴
            total = testService.get_items(file);
        } catch (Throwable e) {
        // 에러시 redirect
            System.out.println(e.getMessage());
            return "redirect:/test";
        }

        // 검출 결과를 보내줄 거면 필요한 부분
        model.addAttribute("result", total);

        // POST 후(이미지 업로드 후) 연결될 페이지
        return "common/test";
    }
}
