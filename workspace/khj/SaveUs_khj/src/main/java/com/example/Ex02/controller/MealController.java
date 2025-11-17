package com.example.Ex02.controller;

import com.example.Ex02.dto.MealDto;
import com.example.Ex02.dto.MealSaveDto;
import com.example.Ex02.mapper.MealMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MealController {

    @Autowired
    private MealMapper mealMapper;

    // 식사 데이터 저장
    @PostMapping("/meal/save")
    public String saveMeal(MealSaveDto dto, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        LocalDate today = LocalDate.now();
        LocalTime time = LocalTime.parse(dto.getMealTime());
        LocalDateTime eatDateTime = LocalDateTime.of(today, time);

        String eatTimeFormatted = eatDateTime.format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        );

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("mealName", dto.getMealName());
        map.put("eatTime", eatTimeFormatted);
        map.put("calories", dto.getCalories());
        map.put("protein", dto.getProtein());
        map.put("carbs", dto.getCarbs());
        map.put("fat", dto.getFat());

        mealMapper.saveMeal(map);
        return "redirect:/dashboard";
    }


    // 식사 기록 수정 페이지 이동
    @GetMapping("/meal/edit/{entryId}")
    public String editMeal(@PathVariable Long entryId, Model model, HttpSession session) {

        // 로그인 여부 확인
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        // 수정할 식사 데이터 조회
        MealDto meal = mealMapper.findMealById(entryId);
        if (meal == null) {
            return "redirect:/dashboard";
        }

        // 본인 식사 데이터인지 확인
        if (!meal.getUserId().equals(userId)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("meal", meal);
        return "meal-edit";
    }

    // 식사 기록 업데이트 처리
    @PostMapping("/meal/update")
    public String updateMeal(MealDto dto, HttpSession session) {

        // 로그인 여부 확인
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        // 본인 식사 데이터로 설정
        dto.setUserId(userId);

        // 사용자가 입력한 시간(HH:mm)에 오늘 날짜 결합
        LocalDate today = LocalDate.now();
        LocalTime time = LocalTime.parse(dto.getMealTime());
        LocalDateTime eatDateTime = LocalDateTime.of(today, time);

        dto.setEatTime(eatDateTime.toString());

        // DB 업데이트
        mealMapper.updateMeal(dto);

        return "redirect:/dashboard";
    }

    @GetMapping("/meal/delete-confirm/{id}")
    public String deleteMealConfirm(@PathVariable Long id) {
        mealMapper.deleteMeal(id);
        return "redirect:/dashboard";
    }


}
