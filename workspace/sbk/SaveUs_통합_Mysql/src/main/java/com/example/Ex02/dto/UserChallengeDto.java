package com.example.Ex02.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class UserChallengeDto {

    private Long userChallengeId;
    private Long challengeId;
    private String title;
    private String description;
    private String imageUrl;

    private String status;
    private Integer progressPercent = 0; // 진행률
    private Integer durationDays;    // 총 기간
    private String metricKey;        // sugar, sodium 등
    private Integer points;

    private java.sql.Date startDate; // 시작 날짜
    private Integer remainingDays;   // 남은 기간

    public Long getUserChallengeId() {
        return userChallengeId;
    }

    public void setUserChallengeId(Long userChallengeId) {
        this.userChallengeId = userChallengeId;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(Integer progressPercent) {
        this.progressPercent = progressPercent;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public String getMetricKey() {
        return metricKey;
    }

    public void setMetricKey(String metricKey) {
        this.metricKey = metricKey;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Integer getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(Integer remainingDays) {
        this.remainingDays = remainingDays;
    }
}
