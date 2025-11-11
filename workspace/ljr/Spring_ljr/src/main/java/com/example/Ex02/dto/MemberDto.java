package com.example.Ex02.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class MemberDto {

    @NotBlank(message = "아이디 입력은 필수입니다.")
    private String id;
    @NotBlank(message = "이름 입력은 필수입니다.")
    private String name;
    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    private String password;
    @NotBlank(message = "성별을 선택해주세요.")
    private String gender;
    @NotEmpty(message = "취미를 하나이상 선택해주세요")
    private List<String> hobby;
    @NotBlank(message = "주소 입력은 필수입니다.")
    private String address;

    @Min(value = 0)
    private int mpoint;

    private String hobbyAsString;

    public String getHobbyAsString() {
        return (hobby != null)? String.join(",", hobby) : null;
    }

    public void setHobbyAsString(String hobbyAsString) {
        this.hobby = Arrays.asList(hobbyAsString.split(","));
    }

    public List<String> getHobby() {
        return hobby;
    }

    public void setHobby(List<String> hobby) {
        this.hobby = hobby;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getMpoint() {
        return mpoint;
    }

    public void setMpoint(int mpoint) {
        this.mpoint = mpoint;
    }
}
