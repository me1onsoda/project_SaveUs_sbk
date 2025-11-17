package com.example.Ex02.mapper;

import com.example.Ex02.dto.UserJoinDto;
import com.example.Ex02.dto.UserMainDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    void insertUser(UserJoinDto user);

    int countByEmail(String email);

    UserJoinDto findByEmail(String email);

    UserJoinDto findById(Long userId);

    UserMainDto findMainInfo(Long userId);
}
