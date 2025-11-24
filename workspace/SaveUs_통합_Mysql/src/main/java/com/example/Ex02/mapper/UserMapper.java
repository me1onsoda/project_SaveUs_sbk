package com.example.Ex02.mapper;

import com.example.Ex02.dto.UserJoinDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    void insertUser(UserJoinDto user);

    int countByEmail(String email);

    UserJoinDto findByEmail(String email);

    UserJoinDto findById(Long userId);

    int countByNickname(String nickname);

    void updateUser(UserJoinDto userDto);

    int checkPassword(Long userId, String currentPassword);

    void updatePassword(Long userId, String newPassword);

    UserJoinDto findMainInfo(Long userId);

    void deleteUserMeals(Long userId);

    void deleteUserGoals(Long userId);

    void deleteUserHealthScores(Long userId);

    void deleteUser(Long userId);

    UserJoinDto findByNicknameAndBirthdate(String nickname, String birthdate);

    void updateWeightAndBmi(Long userId, double weight, double bmi);
}
