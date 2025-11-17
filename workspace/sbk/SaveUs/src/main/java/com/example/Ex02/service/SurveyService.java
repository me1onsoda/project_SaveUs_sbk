package com.example.Ex02.service;

import com.example.Ex02.dto.SurveyDto;
import com.example.Ex02.mapper.SurveyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SurveyService {

    @Autowired
    private SurveyMapper surveyMapper;

    public void processSurvey(SurveyDto dto, Long userId) {

        String fatType = calcFat(dto);
        String proteinType = calcProtein(dto);
        String carbType = calcCarb(dto);

        surveyMapper.insertSurveyResult(userId, fatType, proteinType, carbType);
    }

    public String calcFat(SurveyDto dto) {
        int score = dto.getQ1() + dto.getQ2();
        return type(score);
    }

    public String calcProtein(SurveyDto dto) {
        int score = dto.getQ3() + dto.getQ4();
        return type(score);
    }

    public String calcCarb(SurveyDto dto) {
        int score = dto.getQ5() + dto.getQ6() + dto.getQ7();
        return type(score);
    }

    private String type(int score) {
        if (score <= 3) return "LOW";
        else if (score <= 5) return "MID";
        else return "HIGH";
    }
}
