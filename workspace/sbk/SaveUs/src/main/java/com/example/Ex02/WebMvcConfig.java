package com.example.Ex02;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 로컬 폴더(src/main/resources/static/uploads/posts)에 저장된 이미지를
    // 웹 브라우저가 /uploads/posts/파일명.jpg 로 접근할 수 있게 매핑해주는 설정

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //  file: 접두사는 절대 경로를 의미
        String uploadDir = Paths.get("src/main/resources/static/uploads")
                .toFile().getAbsolutePath();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}