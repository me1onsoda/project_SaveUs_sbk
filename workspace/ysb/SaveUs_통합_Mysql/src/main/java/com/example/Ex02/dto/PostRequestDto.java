package com.example.Ex02.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile; // 임포트

import java.util.List;

@Data
public class PostRequestDto {
    private String content;
    private List<MultipartFile> images;
}