package com.example.Ex02.service;

import com.example.Ex02.dto.DailyIntakeDto;
import com.example.Ex02.dto.UserGoalDto;
import com.example.Ex02.mapper.DailyIntakeMapper;
import com.example.Ex02.mapper.HealthScoreMapper;
import com.example.Ex02.mapper.UserGoalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HealthScoreService {

    @Autowired
    private DailyIntakeMapper dailyIntakeMapper;

    @Autowired
    private UserGoalMapper userGoalMapper;

    @Autowired
    private HealthScoreMapper healthScoreMapper;

    // 전체 날짜 건강점수 재계산
    public void refreshDailyScores(Long userId) {

        // 1. 하루 영양소 집계
        List<DailyIntakeDto> dailyList = dailyIntakeMapper.findDailyIntake(userId);

        // 2. USER_GOAL 조회
        UserGoalDto goal = userGoalMapper.findUserGoal(userId);
        if (goal == null) {
            return; // 목표값이 없으면 계산 불가
        }

        // 3. 하루씩 점수 계산 → DB 저장
        for (DailyIntakeDto intake : dailyList) {

            int score = calculateDailyScore(intake, goal);
            String msg = createStatusMessage(score);

            healthScoreMapper.upsertDailyScore(
                    userId,
                    intake.getEatDate(),
                    score,
                    msg
            );
        }
    }

    // 건강점수 계산 로직
    private int calculateDailyScore(DailyIntakeDto i, UserGoalDto g) {

        int score = 0;

        score += macroScore(i.getCalories(), g.getCaloriesKcal(), 40);
        score += macroScore(i.getProtein(), g.getProteinG(), 20);
        score += macroScore(i.getCarbs(), g.getCarbsG(), 20);
        score += macroScore(i.getFats(), g.getFatsG(), 20);

        return Math.max(0, Math.min(score, 100));
    }

    // 편차에 따른 점수 계산
    private int macroScore(double actual, double target, int max) {
        if (target == 0) return 0;

        double diff = Math.abs(actual - target) / target;

        if (diff <= 0.10) return max;
        if (diff <= 0.20) return max / 2;

        return 0;
    }

    // 상태 메시지
    private String createStatusMessage(int score) {
        if (score >= 80) return "좋음";
        if (score >= 40) return "보통";
        return "개선 필요";
    }

}
