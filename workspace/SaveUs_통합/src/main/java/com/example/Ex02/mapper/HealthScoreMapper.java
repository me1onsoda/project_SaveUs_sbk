package com.example.Ex02.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.time.LocalDate;

@Mapper
public interface HealthScoreMapper {

    void upsertDailyScore(Long userId, LocalDate scoreDate, int score, String statusMessage);

}
