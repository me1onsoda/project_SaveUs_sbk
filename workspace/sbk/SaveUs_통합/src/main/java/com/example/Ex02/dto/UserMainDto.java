package com.example.Ex02.dto;

public class UserMainDto {

    private Long userId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private Integer mainGoal;

    private Integer age;              // USERS.AGE
    private String gender;            // USERS.GENDER
    private Double height;            // USERS.HEIGHT
    private Double currentWeight;     // USERS.CURRENT_WEIGHT

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public Integer getMainGoal() { return mainGoal; }
    public void setMainGoal(Integer mainGoal) { this.mainGoal = mainGoal; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Double getCurrentWeight() { return currentWeight; }
    public void setCurrentWeight(Double currentWeight) { this.currentWeight = currentWeight; }
}
