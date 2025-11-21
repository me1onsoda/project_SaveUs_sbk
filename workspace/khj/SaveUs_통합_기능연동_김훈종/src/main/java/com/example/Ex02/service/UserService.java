package com.example.Ex02.service;

import com.example.Ex02.mapper.UserMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public void deleteUserAll(Long userId) {

        // 1. 자식 테이블 순서대로 삭제
        userMapper.deleteUserGoals(userId);
        userMapper.deleteUserHealthScores(userId);
        userMapper.deleteUserMeals(userId);

        // 2. 마지막에 USERS 삭제
        userMapper.deleteUser(userId);
    }
}
