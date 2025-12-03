package com.example.Ex02.mapper;

import com.example.Ex02.dto.SurveyDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SurveyMapper {

    // 설문 결과 저장
    void saveSurveyResult(
            @Param("userId") Long userId,
            @Param("dto") SurveyDto surveyDto,
            @Param("dietType") String dietType
    );

    // 마이페이지에서 식단 유형 조회
    String findDietType(@Param("userId") Long userId);

}
