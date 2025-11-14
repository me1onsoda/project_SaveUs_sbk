package com.example.Ex02.dto;

import lombok.Data;
import java.util.List;

@Data
public class PostRequestDto {
    private String content;
    // 나중에 이미지 업로드 구현 시 사용
    // private List<String> imageUrls;
}