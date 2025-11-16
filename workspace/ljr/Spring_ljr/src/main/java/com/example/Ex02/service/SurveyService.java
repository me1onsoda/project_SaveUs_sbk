package com.example.Ex02.service;

import com.example.Ex02.dto.SurveyDto;
import com.example.Ex02.mapper.SurveyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SurveyService {

    @Autowired
    private SurveyMapper surveyMapper;

        public void evaluateSurvey(SurveyDto dto) {

            int proteinScore = dto.getProtein1() + dto.getProtein2() + dto.getProtein3();
            int fatScore = dto.getFat1() + dto.getFat2() + dto.getFat3();
            int carbScore = dto.getCarb1() + dto.getCarb2() + dto.getCarb3();

            int threshold = 9;

            dto.setProteinType(proteinScore >= threshold ? "고단백" : "저단백");
            dto.setFatType(fatScore >= threshold ? "고지방" : "저지방");
            dto.setCarbType(carbScore >= threshold ? "고탄수" : "저탄수");
        }
}
