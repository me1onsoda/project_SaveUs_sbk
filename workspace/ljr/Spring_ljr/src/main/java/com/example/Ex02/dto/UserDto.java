package com.example.Ex02.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import javax.management.relation.Role;

public class UserDto {

    private Long userId;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    private String birthdate;
    private Integer   height;
    private Integer   currentWeight;

    @NotNull(message = "건강 목표를 선택해주세요.")
    private Integer mainGoal; // 0=감량 1=증량 2=건강식

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public Integer   getHeight() { return height; }
    public void setHeight(Integer   height) { this.height = height; }

    public Integer   getCurrentWeight() { return currentWeight; }
    public void setCurrentWeight(Integer   currentWeight) { this.currentWeight = currentWeight; }

    public Integer getMainGoal() { return mainGoal; }
    public void setMainGoal(Integer mainGoal) { this.mainGoal = mainGoal; }

}