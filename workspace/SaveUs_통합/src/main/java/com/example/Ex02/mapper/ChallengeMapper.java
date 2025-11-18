package com.example.Ex02.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.Ex02.dto.*; // DTO 패키지 임포트
import java.util.List;
import java.util.Map;

@Mapper
public interface ChallengeMapper {

    List<ChallengeCardDto> findChallengesByGoal(@Param("goal") String goal, @Param("userId") Long userId);
    List<ChallengeCardDto> findChallengesByPersona(@Param("persona") String persona, @Param("userId") Long userId);
    List<TagDto> findAllTags();
    List<LeaderboardEntryDto> findLeaderboardTop5();
    List<ChallengeCardDto> findAllChallengesPaginated(Map<String, Object> params);
    int countAllChallenges(Map<String, Object> params);
}