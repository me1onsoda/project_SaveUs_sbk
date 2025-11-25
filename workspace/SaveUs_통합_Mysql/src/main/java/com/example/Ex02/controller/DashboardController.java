package com.example.Ex02.controller;

import com.example.Ex02.dto.MealDto;
import com.example.Ex02.dto.UserGoalDto;
import com.example.Ex02.dto.UserMainDto;
import com.example.Ex02.mapper.MealMapper;
import com.example.Ex02.mapper.UserGoalMapper;
import com.example.Ex02.mapper.UserMainMapper;
import com.example.Ex02.mapper.UserMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private UserMainMapper userMainMapper;

    @Autowired
    private MealMapper mealMapper;

    @Autowired
    private UserGoalMapper userGoalMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ObesityRateService obesityRateService;

    /**
     * 대시보드 화면
     * 유저 기본정보, 식사정보, 목표치, 섭취량 등을 모두 계산하여 전달
     */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(HttpSession session, Model model) {

        // 세션에서 유저 ID 조회
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        // 유저 정보 조회
        UserMainDto user = userMainMapper.findMainInfo(userId);
        if (user == null) return "redirect:/login";

        // 목표 숫자 -> 목표 이름 변환 (감량, 증량, 건강식)
        String goalName = convertGoalName(user.getMainGoal());

        // 오늘 등록된 모든 식사 목록 조회
        List<MealDto> todayMeals = mealMapper.findTodayMeals(userId);

        // 영양 성분 총합 계산
        int totalProtein = 0, totalCarbs = 0, totalFat = 0;
        int totalSugar = 0, totalFiber = 0, totalCalcium = 0, totalSodium = 0;

        for (MealDto m : todayMeals) {
            totalProtein += m.getProtein() != null ? m.getProtein() : 0;
            totalCarbs += m.getCarbs() != null ? m.getCarbs() : 0;
            totalFat += m.getFat() != null ? m.getFat() : 0;
            totalSugar += m.getSugar() != null ? m.getSugar() : 0;
            totalFiber += m.getFiber() != null ? m.getFiber() : 0;
            totalCalcium += m.getCalcium() != null ? m.getCalcium() : 0;
            totalSodium += m.getSodium() != null ? m.getSodium() : 0;
        }

        // 칼로리 총합 계산
        int totalCalories = todayMeals.stream()
                .mapToInt(m -> m.getCalories() != null ? m.getCalories() : 0)
                .sum();

        // 항상 최신 목표치 재계산 (체중 변경 시 즉시 반영)
        UserGoalDto goal = calculateUserGoal(user);
        goal.setUserId(userId);

        // 사용자 목표치 DB 저장 또는 갱신
        UserGoalDto existingGoal = userGoalMapper.findUserGoal(userId);
        if (existingGoal == null) {
            userGoalMapper.insertUserGoal(goal);
        } else {
            userGoalMapper.updateUserGoal(goal);
        }

        // 저장된 최신 목표치 재조회
        UserGoalDto refreshedGoal = userGoalMapper.findUserGoal(userId);

        // 섭취율(%) 계산
        int percentCalories = goal.getCaloriesKcal() > 0
                ? (int) (totalCalories / (double) goal.getCaloriesKcal() * 100) : 0;

        int percentProtein = goal.getProteinG() > 0
                ? (int) (totalProtein / (double) goal.getProteinG() * 100) : 0;

        int percentCarbs = goal.getCarbsG() > 0
                ? (int) (totalCarbs / (double) goal.getCarbsG() * 100) : 0;

        int percentFat = goal.getFatsG() > 0
                ? (int) (totalFat / (double) goal.getFatsG() * 100) : 0;

        int obesityPercent = obesityRateService.getObesityPercent(userId.intValue());

        model.addAttribute("user", user);
        model.addAttribute("goalName", goalName);
        model.addAttribute("todayMeals", todayMeals);

        model.addAttribute("totalCalories", totalCalories);
        model.addAttribute("totalProtein", totalProtein);
        model.addAttribute("totalCarbs", totalCarbs);
        model.addAttribute("totalFat", totalFat);
        model.addAttribute("totalSugar", totalSugar);
        model.addAttribute("totalFiber", totalFiber);
        model.addAttribute("totalCalcium", totalCalcium);
        model.addAttribute("totalSodium", totalSodium);

        model.addAttribute("goal", goal);
        model.addAttribute("goalCalories", goal.getCaloriesKcal());

        model.addAttribute("percentCalories", percentCalories);
        model.addAttribute("percentProtein", percentProtein);
        model.addAttribute("percentCarbs", percentCarbs);
        model.addAttribute("percentFat", percentFat);

        model.addAttribute("obesityPercent", obesityPercent);

        return "dashboard";
    }

    /**
     * 직접 입력 페이지 이동
     */
    @GetMapping("/direct-input")
    public String directInput() {
        return "direct-input";
    }

    // 목표값(숫자) -> 목표 이름
    private String convertGoalName(Integer mainGoal) {
        if (mainGoal == null) return "기타";
        return switch (mainGoal) {
            case 0 -> "감량";
            case 1 -> "증량";
            case 2 -> "건강식";
            default -> "기타";
        };
    }

    /**
     * 사용자 정보 기반 목표치 계산
     * BMR → TDEE 계산 후, 감량/증량에 따른 보정 적용
     */
    private UserGoalDto calculateUserGoal(UserMainDto user) {

        Double weight = user.getCurrentWeight();
        Double height = user.getHeight();
        Integer age = user.getAge();
        String gender = user.getGender();
        Integer mainGoal = user.getMainGoal();

        // 필수 값 없을 경우 기본 값 제공
        if (weight == null || height == null || age == null || gender == null) {
            UserGoalDto g = new UserGoalDto();
            g.setCaloriesKcal(2400);
            g.setProteinG(90);
            g.setCarbsG(360);
            g.setFatsG(65);
            return g;
        }

        // BMR 계산 (미플린-세인트 조르 공식)
        double bmr = ("M".equalsIgnoreCase(gender))
                ? 10 * weight + 6.25 * height - 5 * age + 5
                : 10 * weight + 6.25 * height - 5 * age - 161;

        // 활동계수: 보통 활동 (1.55)
        double tdee = bmr * 1.55;

        // 목표(감량, 증량)에 따라 TDEE 조정
        if (mainGoal != null) {
            if (mainGoal == 0) tdee -= 300;
            if (mainGoal == 1) tdee += 300;
        }

        // 3대 영양소 비율 적용
        int protein = (int) ((tdee * 0.18) / 4);
        int carbs = (int) ((tdee * 0.55) / 4);
        int fats = (int) ((tdee * 0.27) / 9);

        UserGoalDto goal = new UserGoalDto();
        goal.setCaloriesKcal((int) tdee);
        goal.setProteinG(protein);
        goal.setCarbsG(carbs);
        goal.setFatsG(fats);

        return goal;
    }
}
