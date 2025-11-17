package com.example.Ex02.service;

import com.example.Ex02.dto.SurveyDto;
import com.example.Ex02.mapper.SurveyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SurveyService {

    public void evaluateSurvey(SurveyDto dto) {

        int proteinScore = dto.getProtein1() + dto.getProtein2() + dto.getProtein3();
        int fatScore = dto.getFat1() + dto.getFat2() + dto.getFat3();
        int carbScore = dto.getCarb1() + dto.getCarb2() + dto.getCarb3();

        dto.setProteinType(getTypeWithLabel(proteinScore, "단백"));
        dto.setFatType(getTypeWithLabel(fatScore, "지방"));
        dto.setCarbType(getTypeWithLabel(carbScore, "탄수"));
    }

    private String getTypeBase(int score) {

        if (score <= 7) {
            return "저";
        }
        if (score == 9) {
            return "균형형";
        }
        if (score <= 11) {
            return "중";
        }
        return "고";
    }

    private String getTypeWithLabel(int score, String label) {
        String base = getTypeBase(score);

        if (base.equals("균형형")) {
            return "균형형";
        }

        return base + label;
    }

    // 최종 식단 유형 문자열 생성
    public String getDietType(SurveyDto dto) {

        String p = dto.getProteinType(); // 고단백 / 저단백 / 균형형…
        String f = dto.getFatType();     // 고지방 / 저지방 / 균형형…
        String c = dto.getCarbType();    // 고탄수 / 중탄수 / 균형형…

        // 세 가지 모두 균형형이면
        if (p.equals("균형형") && f.equals("균형형") && c.equals("균형형")) {
            return "완전 균형형 식단";
        }

        // 그 외 조합은 모두 표시
        return p + " / " + f + " / " + c;
    }
}
