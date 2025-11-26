package com.example.Ex02.service;

import com.example.Ex02.dto.*;
import com.example.Ex02.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final MealMapper mealMapper;
    private final HealthScoreMapper healthScoreMapper;
    private final DailyIntakeMapper dailyIntakeMapper;
    private final UserGoalMapper userGoalMapper;
    private final DietTypeService dietTypeService;
    private final RestTemplate restTemplate;

    public ReportDto getReportData(Long userId, String dateStr) {
        ReportDto report = new ReportDto();

        // 1. 데이터 존재 여부 (해당 날짜의 영양소 기록 확인)
        MealDto dailyNutrition = mealMapper.findNutritionByDate(userId, dateStr);
        boolean hasRecord = (dailyNutrition != null && dailyNutrition.getCalories() > 0);
        report.setHasData(hasRecord);

        // -------------------------------------------------
        // [공통] 날짜와 상관없이 보여줄 통계 (트렌드, Top5)
        // -------------------------------------------------

        // A. 건강 점수 추이 (최근 12일)
        List<CalendarScoreDto> scores = healthScoreMapper.selectRecentScores(userId);
        List<String> sDates = new ArrayList<>();
        List<Integer> sValues = new ArrayList<>();
        int sumScore = 0;
        for (CalendarScoreDto s : scores) {
            sDates.add(s.getScoreDate().format(DateTimeFormatter.ofPattern("MM/dd")));
            sValues.add(s.getScore());
            sumScore += s.getScore();
        }
        report.setScoreDates(sDates);
        report.setScoreValues(sValues);
        report.setAverageScore(scores.isEmpty() ? 0 : sumScore / scores.size());

        // B. 식단 유형 변화 (최근 30일)
        List<DailyIntakeDto> dailyList = dailyIntakeMapper.findDailyIntake(userId);

        List<String> dDates = new ArrayList<>();
        List<Integer> cCodes = new ArrayList<>();
        List<Integer> pCodes = new ArrayList<>();
        List<Integer> fCodes = new ArrayList<>();

        int startIdx = Math.max(0, dailyList.size() - 30);
        for (int i = startIdx; i < dailyList.size(); i++) {
            DailyIntakeDto day = dailyList.get(i);
            dDates.add(day.getEatDate().format(DateTimeFormatter.ofPattern("MM/dd")));

            // 총 칼로리 계산 (DB에 있으면 그거 쓰고, 없으면 계산)
            int totalKcal = (day.getCarbs() * 4) + (day.getProtein() * 4) + (day.getFats() * 9);

            // 비율 계산
            double cPct = calcNutrientPercent(day.getCarbs(), 4, totalKcal);
            double pPct = calcNutrientPercent(day.getProtein(), 4, totalKcal);
            double fPct = calcNutrientPercent(day.getFats(), 9, totalKcal);

            // 코드 저장
            cCodes.add(getCarbLevel(cPct));
            pCodes.add(getProteinLevel(pPct));
            fCodes.add(getFatLevel(fPct));
        }

        report.setDietDates(dDates);
        report.setCarbCodes(cCodes);
        report.setProteinCodes(pCodes);
        report.setFatCodes(fCodes);

        // C. 자주 먹은 음식 Top 5
        report.setTopMeals(mealMapper.selectTop5Meals(userId));


        // -------------------------------------------------
        // [개별] 선택된 날짜 기준 상세 데이터 (기록 없으면 스킵)
        // -------------------------------------------------
        if (!hasRecord) return report;

        // 2. 레이더 차트 (목표 대비 %)
        UserGoalDto goal = userGoalMapper.findUserGoal(userId);
        if (goal != null) {
            int carbPct = calcPercent(dailyNutrition.getCarbs(), goal.getCarbsG());
            int protPct = calcPercent(dailyNutrition.getProtein(), goal.getProteinG());
            int fatPct  = calcPercent(dailyNutrition.getFat(), goal.getFatsG());
            int sugarPct = calcPercent(dailyNutrition.getSugar(), 50); // 권장 50g
            int sodiumPct = calcPercent(dailyNutrition.getSodium(), 2000); // 권장 2000mg (WHO 기준)

            report.setRadarMyIntake(Arrays.asList(carbPct, protPct, fatPct, sugarPct, sodiumPct));
            report.setRadarGoal(Arrays.asList(100, 100, 100, 100, 100));
        } else {
            report.setRadarMyIntake(Arrays.asList(0,0,0,0,0));
            report.setRadarGoal(Arrays.asList(100,100,100,100,100));
        }

        // 3. 비만 위험도 (AI 예측)
        try {
            // 그 날짜의 체중 (없으면 최근값)
            Double pastWeight = healthScoreMapper.findWeightByDate(userId, dateStr);
            if(pastWeight == null) pastWeight = 70.0; // 기본값

            String url = "http://3.37.90.119:8001/predict-obesity/" + userId;
            // [디버깅 1] 요청 보내는 URL 확인
            System.out.println("AI Server Request URL: " + url);

            Map<String, Object> result = restTemplate.getForObject(url, Map.class);
            // [디버깅 2] AI 서버 응답값 확인
            System.out.println("AI Server Response: " + result);

            if (result != null && result.get("probability") != null) {
                double prob = Double.parseDouble(result.get("probability").toString());
                System.out.println(prob);
                report.setObesityProbability(prob);
            }
        } catch (Exception e) {
            System.out.println("========== 비만도 분석 에러 발생 ==========");
            report.setObesityProbability(0);
        }

        // 4. 당뇨 예측 결과
        DiabetesScoreDto dScore = mealMapper.selectDiabetesScoreByDate(userId, dateStr);
        if (dScore != null) {
            System.out.println(dScore);
            report.setDiabetesScore(dScore.getScore());
//            report.setDiabetesSimilarity(dScore.getSimilarity());
            report.setDiabetesSimilarity(100-dScore.getScore());
            report.setDiabetesRiskLevel(dScore.getRiskLevel());

            System.out.println(dScore.getScore());
            System.out.println(dScore.getSimilarity());
            System.out.println(dScore.getRiskLevel());

            if ("DANGER".equals(dScore.getRiskLevel()))
                report.setDiabetesComment("경고: 당뇨 위험이 높습니다. 당류 섭취를 줄이세요.");
            else if ("WARNING".equals(dScore.getRiskLevel()))
                report.setDiabetesComment("주의: 식단 관리가 필요합니다. 균형 잡힌 식사를 권장합니다.");
            else
                report.setDiabetesComment("양호: 건강한 식단을 유지하고 계시네요!");
        } else {
            report.setDiabetesRiskLevel("-");
            report.setDiabetesComment("분석 데이터가 없습니다.");
        }

        return report;
    }

    private int calcPercent(Integer actual, Integer target) {
        if (target == null || target == 0) return 0;
        if (actual == null) actual = 0;
        return (int) ((double) actual / target * 100);
    }

    //칼로리에서 영양소 비율 계산
    private double calcNutrientPercent(int grams, int multiplier, int totalKcal) {
        if (totalKcal == 0) return 0.0;
        return ((double) grams * multiplier / totalKcal) * 100.0;
    }
    // 1: Low, 2: Balanced, 3: High

    // 탄수화물 (목표: 40~65%)
    public int getCarbLevel(double carbP) {
        if (carbP < 40.0) return 1;       // 저탄수
        else if (carbP <= 65.0) return 2; // 균형
        else return 3;                    // 고탄수
    }
    // 단백질 (목표: 15~30%)
    public int getProteinLevel(double protP) {
        if (protP < 15.0) return 1;       // 저단백
        else if (protP <= 30.0) return 2; // 균형
        else return 3;                    // 고단백
    }
    // 지방 (목표: 20~35%)
    public int getFatLevel(double fatP) {
        if (fatP < 20.0) return 1;        // 저지방
        else if (fatP <= 35.0) return 2;  // 균형
        else return 3;                    // 고지방
    }


}