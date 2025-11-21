package com.example.Ex02.service;

import com.example.Ex02.dto.*;
import java.util.List;
import java.util.Map;

public interface ChallengeService {

    // "나의 챌린지" 탭 데이터
    MyChallengeSummaryDto getMyChallengeSummary(Long userId);
    List<MyChallengeItemDto> findMyChallengesByStatus(Long userId, String status);

    // "둘러보기" 탭 데이터
    List<ChallengeCardDto> findAiRecommendedChallenges(Long userId);
    List<TagDto> findAllTags();

    // "둘러보기" 탭 - 페이징된 전체 챌린지
    // (Spring Page 객체 대신 Map과 DTO를 사용하여 수동 페이징 처리)
    Map<String, Object> findAllChallengesPaginated(Long userId, String keyword, String tag, int page, int size);

    // 랭킹
    List<LeaderboardEntryDto> findLeaderboardTop5();

    void joinChallenge(Long userId, Long challengeId);

    //매일밤 자동 실행
    void runDailyCheck();

    void restartChallenge(Long userChallengeId);

    // 삭제 메서드
    void deleteChallenge(Long userChallengeId);

    // Top50 메서드
    List<LeaderboardEntryDto> getTop50Leaderboard();


}