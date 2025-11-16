package com.example.Ex02.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
<<<<<<< HEAD
import jakarta.validation.constraints.Size;

public class UserLoginDto {

    @NotBlank(message = "이름은 필수 항목입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

=======

public class UserLoginDto {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    // getter / setter
>>>>>>> home2
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
<<<<<<< HEAD

=======
>>>>>>> home2
