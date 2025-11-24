package com.example.Ex02.dto;

import java.time.LocalDate;

// HEALTH_SCORE_DAILY에서 한 달치 점수를 조회
public class CalendarScoreDto {

    private LocalDate scoreDate;      // 날짜
    private Integer score;            // 건강 점수
    private String statusMessage;     // 상태 메시지

    public LocalDate getScoreDate() {
        return scoreDate;
    }

    public void setScoreDate(LocalDate scoreDate) {
        this.scoreDate = scoreDate;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
