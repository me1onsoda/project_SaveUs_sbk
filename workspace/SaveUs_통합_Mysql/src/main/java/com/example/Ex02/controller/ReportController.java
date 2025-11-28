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

        //식단 저장시 점수도 계산해서 db에 저장 혹은 업데이트
        DailyIntakeDto dailyIntake = dailyIntakeMapper.findDailyIntake(userId).get(0);
        System.out.println("dailyIntake:"+dailyIntake);
        UserGoalDto userGoal = userGoalMapper.findUserGoal(userId);
        System.out.println("userGoal:"+userGoal);
        long score = healthScoreService.calculateDailyScore(dailyIntake,userGoal);
        healthScoreMapper.updateScoreByUserId(userId,new java.sql.Date(System.currentTimeMillis()),score);

        //체중 업데이트 bmi그래프
        Double currentWeight = challengeMapper.getUserWeight(userId);
        healthScoreMapper.updateWeightByUserId(userId, new java.sql.Date(System.currentTimeMillis()),currentWeight);

        // 운동 추천
        ExerciseRecommendResponseDto exerciseData =
                exerciseRecommendService.getExerciseRecommend(Math.toIntExact(userId));

        model.addAttribute("data", exerciseData);   // ← report.html에서 ${data.xxx} 로 사용됨


        return "report";
    }
}