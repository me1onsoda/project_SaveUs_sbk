package com.example.Ex02.mapper;

import com.example.Ex02.dto.UserJoinDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    void insertUser(UserJoinDto user);

    int countByEmail(String email);

    UserJoinDto findByEmail(String email);

    UserJoinDto findById(Long userId);

    void deleteUser(Long userId);

    int countByNickname(String nickname);

    void updateUser(UserJoinDto userDto);

    int checkPassword(Long userId, String currentPassword);

    void updatePassword(Long userId, String newPassword);

    UserJoinDto findMainInfo(Long userId);

}
