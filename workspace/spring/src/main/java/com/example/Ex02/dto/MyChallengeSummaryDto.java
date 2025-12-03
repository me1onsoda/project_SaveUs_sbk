package com.example.Ex02.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MyChallengeSummaryDto {
    private int activeCount;
    private int completedCount;
    private int totalPoints;
}