package com.example.Ex02.mapper;

import com.example.Ex02.dto.DailyIntakeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DailyIntakeMapper {

    // 사용자 하루 섭취 영양소 합산 조회
    List<DailyIntakeDto> findDailyIntake(Long userId);

}
