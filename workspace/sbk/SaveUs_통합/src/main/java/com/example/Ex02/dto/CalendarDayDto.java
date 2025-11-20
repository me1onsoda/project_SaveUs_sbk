package com.example.Ex02.dto;

import java.time.LocalDate;

//캘린더 6×7 그리드 구성용 DTO
public class CalendarDayDto {

    private LocalDate date;        // 날짜
    private Integer score;         // 점수(null 가능)
    private boolean currentMonth;  // 현재 달인지 여부

    public CalendarDayDto(LocalDate date, Integer score, boolean currentMonth) {
        this.date = date;
        this.score = score;
        this.currentMonth = currentMonth;
    }

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

    public boolean isCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(boolean currentMonth) {
        this.currentMonth = currentMonth;
    }
}
