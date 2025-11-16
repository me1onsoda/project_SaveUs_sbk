package com.example.Ex02.mapper;

<<<<<<< HEAD
import com.example.Ex02.dto.UserDto;
=======
import com.example.Ex02.dto.UserJoinDto;
>>>>>>> home2
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

<<<<<<< HEAD
    void insertUser(UserDto user);
    int countByEmail(String email);
    UserDto findByEmail(String email);

    UserDto findById(Long userId);
=======
    void insertUser(UserJoinDto user);

    int countByEmail(String email);

    UserJoinDto findByEmail(String email);

    UserJoinDto findById(Long userId);
>>>>>>> home2
}
