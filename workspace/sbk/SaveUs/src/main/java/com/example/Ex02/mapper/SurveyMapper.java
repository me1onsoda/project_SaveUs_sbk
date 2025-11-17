package com.example.Ex02.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SurveyMapper {
    void insertSurveyResult(
            @Param("userId") Long userId,
            @Param("fatType") String fatType,
            @Param("proteinType") String proteinType,
            @Param("carbType") String carbType
    );
}
