package com.example.Ex02.mapper;

import com.example.Ex02.dto.UserChallengeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserChallengeMapper {

    List<UserChallengeDto> findActiveChallenges(Long userId);
}
