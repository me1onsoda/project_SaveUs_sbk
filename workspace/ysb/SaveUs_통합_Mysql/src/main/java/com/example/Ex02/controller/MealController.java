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

    /**
     * AI 식단 분석 이미지 업로드
     * 이미지 파일을 FastAPI로 전달하여 분석 결과 반환
     */
    @PostMapping("/meal/ai-upload")
    @ResponseBody
    public java.util.List<MealSaveDto> analyzeFoodImage(@RequestParam("file") MultipartFile file) {
        return aiFoodService.analyzeImage(file);
    }

    /**
     * 식사 데이터 저장
     * 사용자 입력 또는 AI 분석으로 전달된 데이터를 DB에 저장
     */
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

    /**
     * 식사 수정 페이지 이동
     * 유저 본인의 항목인지 검증 후 수정 화면으로 이동
     */
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

    /**
     * 식사 데이터 업데이트
     */
    @PostMapping("/meal/update")
    public String updateMeal(MealDto dto, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        dto.setUserId(userId);

        LocalDate today = LocalDate.now();
        LocalTime time = LocalTime.parse(dto.getMealTime());
        LocalDateTime eatDateTime = LocalDateTime.of(today, time);

        dto.setEatTime(eatDateTime);

        mealMapper.updateMeal(dto);

        return "redirect:/dashboard";
    }

    /**
     * 식사 데이터 삭제 (확인 단계를 위한 GET 호출)
     */
    @GetMapping("/meal/delete-confirm/{id}")
    public String deleteMealConfirm(@PathVariable Long id, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        // 삭제 전 해당 Meal 날짜 조회
        MealDto meal = mealMapper.findMealById(id);
        if (meal == null) return "redirect:/dashboard";

        LocalDate date = meal.getEatTime().toLocalDate();

        // 1) 식사 삭제
        mealMapper.deleteMeal(id);

        // 2) 해당 날짜 건강점수 삭제
        mealMapper.deleteDailyScore(userId, date);

        return "redirect:/dashboard";
    }


    /**
     * 음식 검색을 통해 식사 추가
     * 자동 검색 또는 선택한 음식 정보를 DB에 저장
     */
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
