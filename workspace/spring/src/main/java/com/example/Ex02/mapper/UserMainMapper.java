package com.example.Ex02.mapper;

import com.example.Ex02.dto.UserMainDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMainMapper {
    UserMainDto findMainInfo(Long userId);
}
