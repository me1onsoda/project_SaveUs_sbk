package com.example.Ex02.service;

import com.example.Ex02.dto.CalendarDayDto;
import com.example.Ex02.dto.CalendarScoreDto;
import com.example.Ex02.mapper.HealthScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CalendarService {

    @Autowired
    private HealthScoreMapper healthScoreMapper;

    /**
     * 특정 월의 전체 날짜 + 건강점수를 CalendarDayDto 형태로 반환
     */
    public List<CalendarDayDto> getMonthlyCalendar(Long userId, int year, int month) {

        // --- 1) DB에서 한 달치 점수 조회 ---
        List<CalendarScoreDto> scoreList =
                healthScoreMapper.findScoresOfMonth(userId, year, month);

        Map<LocalDate, CalendarScoreDto> scoreMap = new HashMap<>();
        for (CalendarScoreDto dto : scoreList) {
            scoreMap.put(dto.getScoreDate(), dto);
        }

        // --- 2) 달력 날짜 구성 시작 ---
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstDay = yearMonth.atDay(1);
        int daysInMonth = yearMonth.lengthOfMonth();

        List<CalendarDayDto> calendarDays = new ArrayList<>();

        // --------- 핵심 수정: 요일 offset 계산 ---------
        // Java dayOfWeek: MON=1 ... SUN=7
        // 달력에서는 SUN=0, MON=1 ... SAT=6 이어야 정렬이 깨지지 않음
        int dayOfWeek = firstDay.getDayOfWeek().getValue(); // 1~7
        int offset = (dayOfWeek == 7) ? 0 : dayOfWeek;      // 7(일요일) → 0

        // --- 3) 앞쪽 빈칸 삽입 ---
        for (int i = 0; i < offset; i++) {
            calendarDays.add(new CalendarDayDto()); // date==null → HTML에서 빈칸 처리
        }

        // --- 4) 실제 날짜 데이터 추가 ---
        for (int day = 1; day <= daysInMonth; day++) {

            LocalDate date = LocalDate.of(year, month, day);

            CalendarDayDto dto = new CalendarDayDto();
            dto.setDate(date);

            CalendarScoreDto scoreDto = scoreMap.get(date);

            if (scoreDto != null) {
                int score = scoreDto.getScore();

                dto.setScore(score);
                dto.setStatusMessage(scoreDto.getStatusMessage());
                dto.setColor(getColorByScore(score));

            } else {
                dto.setScore(null);
                dto.setStatusMessage("기록 없음");
                dto.setColor(null); // 빈칸은 색 없음
            }

            calendarDays.add(dto);
        }

        return calendarDays;
    }


    /**
     * 점수에 따른 색상 선택
     */
    private String getColorByScore(int score) {

        if (score >= 80) {
            return "#6BCB77";   // 초록
        }
        if (score >= 40) {
            return "#FFD93D";   // 노랑
        }
        return "#FF6B6B";       // 빨강
    }

}
