package com.example.Ex02.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class UserBadgeDto {
    private Long badgeId;
    private Long challengeId;
    private String title;
    private String description;
    private String imageUrl;
    private java.sql.Date acquiredAt;

    public Long getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(Long badgeId) {
        this.badgeId = badgeId;
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

    public Date getAcquiredAt() {
        return acquiredAt;
    }

    public void setAcquiredAt(Date acquiredAt) {
        this.acquiredAt = acquiredAt;
    }
}
