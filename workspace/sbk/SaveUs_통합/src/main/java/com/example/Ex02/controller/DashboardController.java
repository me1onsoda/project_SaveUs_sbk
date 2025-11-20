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

    @GetMapping("/direct-input")
    public String directInput() {
        return "direct-input";
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        UserMainDto user = userMainMapper.findMainInfo(userId);
        if (user == null) return "redirect:/login";

        String goalName = convertGoalName(user.getMainGoal());
        List<MealDto> todayMeals = mealMapper.findTodayMeals(userId);

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

        int totalCalories = todayMeals.stream()
                .mapToInt(m -> m.getCalories() != null ? m.getCalories() : 0)
                .sum();

        UserGoalDto goal = userGoalMapper.findUserGoal(userId);
        if (goal == null) {
            goal = calculateUserGoal(user);
            goal.setUserId(userId);
            userGoalMapper.insertUserGoal(goal);
        }

        /* 🟢 Progress bar percentage 계산 */
        int percentCalories = goal.getCaloriesKcal() > 0
                ? (int) (totalCalories / (double) goal.getCaloriesKcal() * 100)
                : 0;

        int percentProtein = goal.getProteinG() > 0
                ? (int) (totalProtein / (double) goal.getProteinG() * 100)
                : 0;

        int percentCarbs = goal.getCarbsG() > 0
                ? (int) (totalCarbs / (double) goal.getCarbsG() * 100)
                : 0;

        int percentFat = goal.getFatsG() > 0
                ? (int) (totalFat / (double) goal.getFatsG() * 100)
                : 0;

        model.addAttribute("user", user);
        model.addAttribute("goalName", goalName);
        model.addAttribute("todayMeals", todayMeals);

        model.addAttribute("totalCalories", totalCalories);
        model.addAttribute("totalProtein", totalProtein);
        model.addAttribute("totalCarbs", totalCarbs);
        model.addAttribute("totalFat", totalFat);

        model.addAttribute("goal", goal);
        model.addAttribute("goalCalories", goal.getCaloriesKcal());

        model.addAttribute("percentCalories", percentCalories);
        model.addAttribute("percentProtein", percentProtein);
        model.addAttribute("percentCarbs", percentCarbs);
        model.addAttribute("percentFat", percentFat);

        return "dashboard";
    }

    private String convertGoalName(Integer mainGoal) {
        if (mainGoal == null) return "기타";
        return switch (mainGoal) {
            case 0 -> "감량";
            case 1 -> "증량";
            case 2 -> "건강식";
            default -> "기타";
        };
    }

    private UserGoalDto calculateUserGoal(UserMainDto user) {

        Double weight = user.getCurrentWeight();
        Double height = user.getHeight();
        Integer age = user.getAge();
        String gender = user.getGender();
        Integer mainGoal = user.getMainGoal();

        if (weight == null || height == null || age == null || gender == null) {
            UserGoalDto g = new UserGoalDto();
            g.setCaloriesKcal(2400);
            g.setProteinG(90);
            g.setCarbsG(360);
            g.setFatsG(65);
            return g;
        }

        double bmr = ("M".equalsIgnoreCase(gender))
                ? 10 * weight + 6.25 * height - 5 * age + 5
                : 10 * weight + 6.25 * height - 5 * age - 161;

        double tdee = bmr * 1.55;

        if (mainGoal != null) {
            if (mainGoal == 0) tdee -= 300;
            if (mainGoal == 1) tdee += 300;
        }

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
