package com.example.Ex02.mapper;

import com.example.Ex02.dto.SurveyDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SurveyMapper {

    void insertSurvey(
            @Param("userId") Long userId,
            @Param("survey") SurveyDto surveyDto,
            @Param("fatType") String fatType,
            @Param("proteinType") String proteinType,
            @Param("carbType") String carbType
    );

    SurveyDto findByUserId(Long userId);
}
