package com.example.Ex02.mapper;

import com.example.Ex02.dto.UserBadgeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserBadgeMapper {

    List<UserBadgeDto> findBadgesByUser(Long userId);
}
