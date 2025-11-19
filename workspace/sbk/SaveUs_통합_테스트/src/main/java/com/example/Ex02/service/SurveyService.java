package com.example.Ex02.service;

import com.example.Ex02.dto.SurveyDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SurveyService {

    public void evaluateSurvey(SurveyDto dto) {

        // 단백질
        int proteinScore = dto.getProtein1() + dto.getProtein2() + dto.getProtein3();
        // 지방
        int fatScore     = dto.getFat1() + dto.getFat2() + dto.getFat3();
        // 탄수화물
        int carbScore    = dto.getCarb1() + dto.getCarb2() + dto.getCarb3();

        dto.setProteinType(getTypeWithLabel(proteinScore, "단백"));
        dto.setFatType(getTypeWithLabel(fatScore, "지방"));
        dto.setCarbType(getTypeWithLabel(carbScore, "탄수"));
    }

    private String getTypeBase(int score) {

        if (score <= 6) {
            return "저";
        } else if (score <= 10) {
            return "중";  // 중간 영역 → 균형형
        } else {
            return "고";
        }
    }

    private String getTypeWithLabel(int score, String label) {
        String base = getTypeBase(score);

        if (base.equals("중")) {
            return "균형형";
        }

        return base + label;
    }

    public String getDietType(SurveyDto dto) {

        String p = dto.getProteinType();
        String f = dto.getFatType();
        String c = dto.getCarbType();

        // 모두 균형형
        List<String> TypeOfMeal = new ArrayList<String>();
        if (p.equals("균형형") && f.equals("균형형") && c.equals("균형형")) {
            TypeOfMeal.add("완전 균형형 식단");
            return "완전 균형형 식단";
        }
        // 단백질만 균형형 → 나머지 2개만 반환
        if (!p.equals("균형형")){
            TypeOfMeal.add(p);
        }
        // 지방만 균형형
        if (!f.equals("균형형")){
            TypeOfMeal.add(f);
        }
        // 탄수만 균형형
        if (!c.equals("균형형")){
            TypeOfMeal.add(c);
        }

        return String.join("/", TypeOfMeal);
    }
}
