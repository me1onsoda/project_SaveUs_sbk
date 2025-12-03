package com.example.Ex02.mapper;

import com.example.Ex02.dto.CalendarScoreDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HealthScoreMapper {

    // 점수 저장/업데이트
    void upsertDailyScore(
            @Param("userId") Long userId,
            @Param("scoreDate") Date scoreDate,
            @Param("score") int score,
            @Param("statusMessage") String statusMessage
    );

    // 특정 월의 건강점수 조회
    List<CalendarScoreDto> findScoresOfMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );

    void upsertDailyWeight(@Param("userId") Long userId, @Param("weight") double weight);
    List<CalendarScoreDto> selectRecentScores(@Param("userId") Long userId);
    Double findWeightByDate(@Param("userId") Long userId, @Param("date") String date);

    //음식 저장시에 score업데이트(리포트페이지에서 활용)
    void updateScoreByUserId(@Param("userId") Long userId,
            @Param("scoreDate") Date scoreDate,
            @Param("score") long score
    );

    void updateWeightByUserId(
            @Param("userId") Long userId,
            @Param("scoreDate") Date scoreDate,
            @Param("weight") Double weight
    );
}
