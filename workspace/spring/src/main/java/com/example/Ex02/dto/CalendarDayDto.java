package com.example.Ex02.dto;

import java.time.LocalDate;

public class CalendarDayDto {

    private LocalDate date;
    private Integer score;        // 점수 없을 수도 있어 null 처리
    private String color;         // 점수 색상
    private String statusMessage; // 상태 설명

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
