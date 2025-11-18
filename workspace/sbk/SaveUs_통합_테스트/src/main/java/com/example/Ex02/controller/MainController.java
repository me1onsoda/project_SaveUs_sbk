package com.example.Ex02.controller;

import com.example.Ex02.dto.MealDto;
import com.example.Ex02.dto.UserGoalDto;
import com.example.Ex02.dto.UserMainDto;
import com.example.Ex02.mapper.MealMapper;
import com.example.Ex02.mapper.UserGoalMapper;
import com.example.Ex02.mapper.UserMainMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    private UserMainMapper userMainMapper;

    @Autowired
    private MealMapper mealMapper;

    @Autowired
    private UserGoalMapper userGoalMapper;

    // 직접 입력 페이지 매핑 추가
    @GetMapping("/direct-input")
    public String directInput() {
        return "direct-input";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        // 로그인 체크
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        // 사용자 정보 조회
        UserMainDto user = userMainMapper.findMainInfo(userId);
        if (user == null) {
            return "redirect:/login";
        }

        // 사용자 메인 목표 이름 변환
        String goalName = convertGoalName(user.getMainGoal());

        // 오늘의 식사 조회
        List<MealDto> todayMeals = mealMapper.findTodayMeals(userId);

        // 오늘 섭취한 영양소 합계 계산
        int totalProtein = 0;
        int totalCarbs = 0;
        int totalFat = 0;

        for (MealDto m : todayMeals) {
            totalProtein += (m.getProtein() != null ? m.getProtein() : 0);
            totalCarbs += (m.getCarbs() != null ? m.getCarbs() : 0);
            totalFat += (m.getFat() != null ? m.getFat() : 0);
        }

        // 오늘의 총 칼로리 계산
        int totalCalories = 0;
        for (MealDto m : todayMeals) {
            totalCalories += (m.getCalories() != null ? m.getCalories() : 0);
        }

        // 사용자 목표 조회 (없으면 자동 생성)
        UserGoalDto goal = userGoalMapper.findUserGoal(userId);

        // 목표가 없으면 BMR 기반으로 자동 생성
        if (goal == null) {
            goal = calculateUserGoal(user);
            goal.setUserId(userId);
            userGoalMapper.insertUserGoal(goal);
        }

        // 모델에 데이터 담기
        model.addAttribute("user", user);
        model.addAttribute("goalName", goalName);
        model.addAttribute("todayMeals", todayMeals);

        model.addAttribute("totalProtein", totalProtein);
        model.addAttribute("totalCarbs", totalCarbs);
        model.addAttribute("totalFat", totalFat);

        model.addAttribute("goal", goal);

        model.addAttribute("totalCalories", totalCalories);
        model.addAttribute("goalCalories", goal.getCaloriesKcal());

        return "dashboard";
    }

    // 메인 목표 코드(정수) → 문자열 변환
    private String convertGoalName(Integer mainGoal) {
        if (mainGoal == null) return "기타";

        switch (mainGoal) {
            case 0: return "감량";
            case 1: return "증량";
            case 2: return "건강식";
            default: return "기타";
        }
    }

    // BMR 기반 목표 자동 계산
    private UserGoalDto calculateUserGoal(UserMainDto user) {

        Double weight = user.getCurrentWeight();
        Double height = user.getHeight();
        Integer age = user.getAge();
        String gender = user.getGender();
        Integer mainGoal = user.getMainGoal(); // 0:감량, 1:증량, 2:건강식(유지)

        // 값이 부족하면 기본 목표로 처리
        if (weight == null || height == null || age == null || gender == null) {
            UserGoalDto goal = new UserGoalDto();
            goal.setCaloriesKcal(2400);
            goal.setProteinG(90);
            goal.setCarbsG(360);
            goal.setFatsG(65);
            return goal;
        }

        // 남녀 BMR(기초대사량) 공식
        double bmr;
        if ("M".equalsIgnoreCase(gender)) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }

        // 활동계수는 1.55(일반 직장인 기준)로 통일하여 일관성 강화
        // 일반적으로 하루 6000~8000보 걷는 수준
        double activityLevel = 1.55;

        // 총 에너지 소비량(TDEE) = BMR(기초대사량) * 활동계수(1.55)
        double tdee = bmr * activityLevel;

        // 목표에 따른 칼로리 조정
        // 국제 기준(WHO/ACSM)과 대부분의 건강 앱 기준
        // 감량: -300kcal / 증량: +300kcal / 건강식: 그대로
        if (mainGoal != null) {
            if (mainGoal == 0) {
                tdee -= 300;      // 감량
            } else if (mainGoal == 1) {
                tdee += 300;      // 증량
            }
        }

        // 한국인 영양섭취기준(KDRI)의 정상 범위 중 중간값으로 비율 통일
        // 단백질: 18%, 지방: 27%, 탄수화물: 55%
        double proteinRate = 0.18;
        double fatRate = 0.27;
        double carbRate = 0.55;

        // 영양소 g 단위 변환
        // 단백질/탄수화물: kcal ÷ 4
        // 지방: kcal ÷ 9
        int protein = (int) Math.round((tdee * proteinRate) / 4);
        int carbs = (int) Math.round((tdee * carbRate) / 4);
        int fats = (int) Math.round((tdee * fatRate) / 9);

        UserGoalDto goal = new UserGoalDto();
        goal.setCaloriesKcal((int) Math.round(tdee));
        goal.setProteinG(protein);
        goal.setCarbsG(carbs);
        goal.setFatsG(fats);

        return goal;
    }


}
