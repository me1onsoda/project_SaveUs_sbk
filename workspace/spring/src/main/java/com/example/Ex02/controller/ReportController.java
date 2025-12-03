package com.example.Ex02.controller;

import com.example.Ex02.dto.DailyIntakeDto;
import com.example.Ex02.dto.ExerciseRecommendResponseDto;
import com.example.Ex02.dto.UserGoalDto;
import com.example.Ex02.mapper.ChallengeMapper;
import com.example.Ex02.mapper.DailyIntakeMapper;
import com.example.Ex02.mapper.HealthScoreMapper;
import com.example.Ex02.mapper.UserGoalMapper;
import com.example.Ex02.service.ExerciseRecommendService;
import com.example.Ex02.service.HealthScoreService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private HealthScoreMapper healthScoreMapper;

    @Autowired
    private HealthScoreService healthScoreService;

    @Autowired
    private ExerciseRecommendService exerciseRecommendService;

    @Autowired
    private DailyIntakeMapper dailyIntakeMapper;

    @Autowired
    private UserGoalMapper userGoalMapper;

    @Autowired
    private ChallengeMapper challengeMapper;

    @GetMapping
    public String showReportPage(HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        // 1) 오늘 식단 기록 조회
        List<DailyIntakeDto> intakeList = dailyIntakeMapper.findDailyIntake(userId);

        if (intakeList != null && !intakeList.isEmpty()) {
            DailyIntakeDto dailyIntake = intakeList.get(intakeList.size()-1);
            UserGoalDto userGoal = userGoalMapper.findUserGoal(userId);
            System.out.println(dailyIntake.getCalories());
            System.out.println(dailyIntake.getCarbs());
            System.out.println(dailyIntake.getFats());
            System.out.println(dailyIntake.getProtein());

            System.out.println(userGoal.getCaloriesKcal());
            System.out.println(userGoal.getCarbsG());
            System.out.println(userGoal.getFatsG());
            System.out.println(userGoal.getProteinG());

            long score = healthScoreService.calculateDailyScore(dailyIntake, userGoal);
            System.out.println("score"+score);
            healthScoreMapper.updateScoreByUserId(
                    userId,
                    new java.sql.Date(System.currentTimeMillis()),
                    score
            );
        } else {
            System.out.println("[INFO] 오늘 식단 기록 없음 → 점수 계산 생략");
        }

        // 2) 체중 있는 경우만 BMI 업데이트
        Double currentWeight = challengeMapper.getUserWeight(userId);
        if (currentWeight != null) {
            healthScoreMapper.updateWeightByUserId(
                    userId,
                    new java.sql.Date(System.currentTimeMillis()),
                    currentWeight
            );
        }

        // 3) 운동 추천 데이터
        ExerciseRecommendResponseDto exerciseData =
                exerciseRecommendService.getExerciseRecommend(Math.toIntExact(userId));

        model.addAttribute("userId", userId);
        model.addAttribute("data", exerciseData);

        return "report";
    }
}
