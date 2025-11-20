package com.example.Ex02.service;

import com.example.Ex02.dto.DailyIntakeDto;
import com.example.Ex02.dto.UserGoalDto;
import com.example.Ex02.mapper.DailyIntakeMapper;
import com.example.Ex02.mapper.HealthScoreMapper;
import com.example.Ex02.mapper.UserGoalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthScoreService {

    @Autowired
    private DailyIntakeMapper dailyIntakeMapper;

    @Autowired
    private UserGoalMapper userGoalMapper;

    @Autowired
    private HealthScoreMapper healthScoreMapper;


    /**
     * 사용자 전체 날짜에 대해 건강점수를 재계산하고 DB에 저장
     */
    public void refreshDailyScores(Long userId) {

        List<DailyIntakeDto> dailyList = dailyIntakeMapper.findDailyIntake(userId);
        UserGoalDto goal = userGoalMapper.findUserGoal(userId);

        if (goal == null || dailyList == null) return;

        for (DailyIntakeDto intake : dailyList) {

            int score = calculateDailyScore(intake, goal);
            String msg = createStatusMessage(score);

            healthScoreMapper.upsertDailyScore(
                    userId,
                    java.sql.Date.valueOf(intake.getEatDate()),
                    score,
                    msg
            );
        }
    }


    /**
     * 하루 섭취량을 기반으로 총 건강점수를 계산
     * - 칼로리, 탄수화물, 단백질, 지방을 각각 25점씩 배분
     * - 달성률 기반 점수 + 과다 섭취 패널티 방식을 적용
     */
    private int calculateDailyScore(DailyIntakeDto d, UserGoalDto g) {

        int score = 0;

        score += macroScorePenalty(d.getCalories(), g.getCaloriesKcal(), 25);
        score += macroScorePenalty(d.getCarbs(), g.getCarbsG(), 25);
        score += macroScorePenalty(d.getProtein(), g.getProteinG(), 25);
        score += macroScorePenalty(d.getFats(), g.getFatsG(), 25);

        return Math.min(score, 100);
    }


    /**
     * 영양소 달성률에 따른 점수 계산 함수.
     *
     * 계산 방식:
     *  - 목표 이하(0~100%)      : 달성률에 비례하여 점수 부여
     *  - 목표 초과(100~120%)   : 최대 50%까지 패널티 적용
     *  - 과다 초과(120~150%)   : 추가 패널티 적용 (0점까지 감소)
     *  - 매우 과다(150% 이상)  : 0점 처리
     *
     * @param actual 섭취량
     * @param target 목표량
     * @param max    해당 영양소의 배점 (25점)
     */
    private int macroScorePenalty(double actual, double target, int max) {

        if (target <= 0) return 0;

        double ratio = actual / target;

        // (1) 목표 이하 → 비례 점수
        if (ratio <= 1.0) {
            return (int) Math.round(max * ratio);
        }

        // (2) 100~120% 구간 → 절반(50%)까지 감점
        if (ratio <= 1.2) {
            double penaltyRate = (ratio - 1.0) / 0.2;        // 0~1
            double penalty = max * 0.5 * penaltyRate;       // 최대 50% 패널티
            return (int) Math.round(max - penalty);
        }

        // (3) 120~150% 구간 → 최대 100%까지 감점
        if (ratio <= 1.5) {
            double penaltyRate = (ratio - 1.2) / 0.3;        // 0~1
            double penalty = max * 0.5 * penaltyRate;       // 추가 패널티
            return (int) Math.round(max * 0.5 - penalty);
        }

        // (4) 150% 초과 → 0점
        return 0;
    }


    /**
     * 점수 범위에 따른 상태 메시지 생성.
     */
    private String createStatusMessage(int score) {
        if (score >= 80) return "좋음";
        if (score >= 40) return "보통";
        return "개선 필요";
    }

    /**
     * 특정 연도·월에 대해 건강점수를 전부 자동 계산하여 DB에 저장
     */
    public void calculateMonthlyScore(Long userId, int year, int month) {

        List<DailyIntakeDto> dailyList =
                dailyIntakeMapper.findDailyIntakeForMonth(userId, year, month);

        UserGoalDto goal = userGoalMapper.findUserGoal(userId);

        if (goal == null || dailyList == null) return;

        for (DailyIntakeDto intake : dailyList) {

            int score = calculateDailyScore(intake, goal);
            String msg = createStatusMessage(score);

            healthScoreMapper.upsertDailyScore(
                    userId,
                    java.sql.Date.valueOf(intake.getEatDate()),
                    score,
                    msg
            );
        }
    }




}
