package com.example.Ex02.controller;

import com.example.Ex02.dto.MealDto;
import com.example.Ex02.dto.MealSaveDto;
import com.example.Ex02.mapper.MealMapper;
import com.example.Ex02.service.AiFoodService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MealController {

    @Autowired
    private MealMapper mealMapper;

    @Autowired
    private AiFoodService aiFoodService;

    @PostMapping("/meal/ai-upload")
    @ResponseBody
    public java.util.List<MealSaveDto> analyzeFoodImage(@RequestParam("file") MultipartFile file) {
        return aiFoodService.analyzeImage(file);
    }

    // 식사 데이터 저장
    @PostMapping("/meal/save")
    public String saveMeal(MealSaveDto dto, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        LocalDate today = LocalDate.now();
        LocalTime time = LocalTime.parse(dto.getMealTime());
        LocalDateTime eatDateTime = LocalDateTime.of(today, time);

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("mealName", dto.getMealName());
        map.put("eatTime", eatDateTime); // LocalDateTime 그대로 전달

        map.put("calories", dto.getCalories());
        map.put("protein", dto.getProtein());
        map.put("carbs", dto.getCarbs());
        map.put("fat", dto.getFat());
        map.put("sugar", dto.getSugar());
        map.put("fiber", dto.getFiber());
        map.put("calcium", dto.getCalcium());
        map.put("sodium", dto.getSodium());

        mealMapper.saveMeal(map);
        return "redirect:/dashboard";
    }

    // 수정 페이지 이동
    @GetMapping("/meal/edit/{entryId}")
    public String editMeal(@PathVariable Long entryId, Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        MealDto meal = mealMapper.findMealById(entryId);
        if (meal == null) return "redirect:/dashboard";
        if (!meal.getUserId().equals(userId)) return "redirect:/dashboard";

        model.addAttribute("meal", meal);
        return "meal-edit";
    }

    // 식사 업데이트
    @PostMapping("/meal/update")
    public String updateMeal(MealDto dto, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        dto.setUserId(userId);

        LocalDate today = LocalDate.now();
        LocalTime time = LocalTime.parse(dto.getMealTime());
        LocalDateTime eatDateTime = LocalDateTime.of(today, time);

        dto.setEatTime(eatDateTime); // 문자열 제거

        mealMapper.updateMeal(dto);

        return "redirect:/dashboard";
    }

    @GetMapping("/meal/delete-confirm/{id}")
    public String deleteMealConfirm(@PathVariable Long id) {
        mealMapper.deleteMeal(id);
        return "redirect:/dashboard";
    }

    // 음식검색으로 추가
    @PostMapping("/meal/add-from-food")
    @ResponseBody
    public String addMealFromFood(@RequestBody MealSaveDto dto, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "NOT_LOGIN";

        LocalDateTime eatDateTime = LocalDateTime.now();

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("mealName", dto.getMealName());
        map.put("eatTime", eatDateTime);

        map.put("calories", dto.getCalories());
        map.put("carbs", dto.getCarbs());
        map.put("protein", dto.getProtein());
        map.put("fat", dto.getFat());
        map.put("sugar", dto.getSugar());
        map.put("fiber", dto.getFiber());
        map.put("calcium", dto.getCalcium());
        map.put("sodium", dto.getSodium());

        mealMapper.saveMeal(map);

        return "OK";
    }

}
