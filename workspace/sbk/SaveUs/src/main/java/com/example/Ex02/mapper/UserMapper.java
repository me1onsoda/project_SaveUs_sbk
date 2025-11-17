package com.example.Ex02.mapper;

import com.example.Ex02.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    void insertUser(UserDto user);
    int countByEmail(String email);
    UserDto findByEmail(String email);
    UserDto findById(Long userId);
}
