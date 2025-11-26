package com.example.Ex02.dto;

import lombok.Data;

@Data
public class EmailVerificationStatusDto {
    private boolean resent;
    private long remainSeconds;
}
