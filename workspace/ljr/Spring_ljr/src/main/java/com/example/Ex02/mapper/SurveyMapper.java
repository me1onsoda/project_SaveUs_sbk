package com.example.Ex02.mapper;

<<<<<<< HEAD
=======
import com.example.Ex02.dto.SurveyDto;
>>>>>>> home2
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SurveyMapper {
<<<<<<< HEAD
    void insertSurveyResult(
            @Param("userId") Long userId,
=======

    void insertSurvey(
            @Param("userId") Long userId,
            @Param("survey") SurveyDto surveyDto,
>>>>>>> home2
            @Param("fatType") String fatType,
            @Param("proteinType") String proteinType,
            @Param("carbType") String carbType
    );
<<<<<<< HEAD
=======

    SurveyDto findByUserId(Long userId);
>>>>>>> home2
}
