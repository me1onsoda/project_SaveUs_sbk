package com.example.Ex02.mapper;

import com.example.Ex02.dto.UserGoalDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserGoalMapper {
    // 목표 조회
    UserGoalDto findUserGoal(Long userId);
    // 목표 추가
    void insertUserGoal(UserGoalDto dto);
}
