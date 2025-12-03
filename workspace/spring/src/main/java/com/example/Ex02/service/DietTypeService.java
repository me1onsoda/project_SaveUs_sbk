package com.example.Ex02.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DietTypeService {

    // 각 영양소 기준값에 따른 유형 반환
    private String getProteinType(int gram) {
        if (gram <= 40) return "저단백";
        else if (gram <= 90) return "균형형";
        else return "고단백";
    }

    private String getFatType(int gram) {
        if (gram <= 35) return "저지방";
        else if (gram <= 70) return "균형형";
        else return "고지방";
    }

    private String getCarbType(int gram) {
        if (gram <= 120) return "저탄수";
        else if (gram <= 250) return "균형형";
        else return "고탄수";
    }

    // 하루 식단유형 통합 판단
    public String getDailyDietType(int protein, int fat, int carbs) {

        String p = getProteinType(protein);
        String f = getFatType(fat);
        String c = getCarbType(carbs);

        // 3개 모두 균형형
        if (p.equals("균형형") && f.equals("균형형") && c.equals("균형형")) {
            return "완전균형형";
        }

        // 균형형이 아닌 항목만 반환
        List<String> types = new ArrayList<>();

        if (!p.equals("균형형")) types.add(p);
        if (!f.equals("균형형")) types.add(f);
        if (!c.equals("균형형")) types.add(c);

        return String.join("/", types);
    }
}