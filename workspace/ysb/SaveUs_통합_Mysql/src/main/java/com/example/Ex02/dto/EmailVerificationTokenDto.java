package com.example.Ex02.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmailVerificationTokenDto {
    private Long tokenId;
    private Long userId;
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
