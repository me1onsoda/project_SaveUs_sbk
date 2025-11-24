package com.example.Ex02.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class UserJoinDto {

    private Long userId;

    @NotNull(message = "이메일을 입력하세요.")
    private String email;

    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 12, message = "닉네임은 2~12자 사이여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣_]+$", message = "닉네임은 한글,영문,숫자, _ 만 사용할 수 있습니다.")
    private String nickname;

    @NotNull(message = "키를 입력해주세요.")
    @Min(value = 0, message = "다시 입력해주세요.")
    @Max(value = 250, message = "다시 입력해주세요.")
    private Integer height;

    @NotNull(message = "몸무게를 입력해주세요.")
    @Min(value = 1, message = "다시 입력해주세요.")
    @Max(value = 300, message = "다시 입력해주세요.")
    private Integer currentWeight;

    @NotNull(message = "목표를 선택해주세요.")
    private Integer mainGoal;

    private String profileImageUrl = "/images/icon/mypage.png";

    // 추가: 생년월일 (YYYY-MM-DD)
    @NotBlank(message = "생년월일은 필수입니다.")
    private String birthdate;

    // 추가: 나이
    private Integer age;

    // 추가: 성별
    @NotBlank(message = "성별을 선택해주세요.")
    private String gender;

    private LocalDateTime createdAt;
    private float bmi;

    public float getBmi() {
        if (height == null || currentWeight == null || height == 0) {
            return 0f;
        }

        double heightMeter = height / 100.0;
        double bmiValue = currentWeight / (heightMeter * heightMeter);

        return (float) (Math.round(bmiValue * 10) / 10.0);
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }

    public Integer getCurrentWeight() { return currentWeight; }
    public void setCurrentWeight(Integer currentWeight) { this.currentWeight = currentWeight; }

    public Integer getMainGoal() { return mainGoal; }
    public void setMainGoal(Integer mainGoal) { this.mainGoal = mainGoal; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

}
