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

    // "나의 챌린지" - 요약 카드
    Integer countMyChallengesByStatus(@Param("userId") Long userId, @Param("status") String status);
    // "나의 챌린지" - 요약 카드 (USERS 테이블에 TOTAL_POINTS가 있다고 가정) (TOTAL_POINTS를 추가?)
    Integer getMyTotalPoints(@Param("userId") Long userId);
    // "나의 챌린지" - 진행중/완료 목록
    List<MyChallengeItemDto> findMyChallengesByStatus(@Param("userId") Long userId, @Param("status") String status);
    // "둘러보기" - 추천 (사용자의 MAIN_GOAL 조회)
    //String findUserMainGoal(@Param("userId") Long userId);
    Integer findUserMainGoal(@Param("userId") Long userId);

    void joinChallenge(@Param("userId") Long userId,
                       @Param("challengeId") Long challengeId,
                       @Param("startValue") Double startValue);
    Double getUserWeight(Long userId);
    ChallengeCardDto findChallengeDetail(Long challengeId);

    // 현재 진행 중인('ONGOING') 모든 챌린지 내역 가져오기
    List<MyChallengeItemDto> findAllActiveChallenges();

    // 성공시 카운트와 퍼센트를 동시에 업데이트
    void updateProgress(@Param("userChallengeId") Long userChallengeId,
                        @Param("currentCount") int currentCount,
                        @Param("progressPercent") int progressPercent);

    // 챌린지 최종 완료 처리 (STATUS = 'COMPLETED')
    void completeChallenge(Long userChallengeId);

    // 챌린지 실패 처리 (스케줄러에서 사용)
    void failChallenge(Long userChallengeId);

    // 내 챌린지 기록 1개 상세 조회 (재시작 시 정보 확인용)
    MyChallengeItemDto findUserChallengeById(Long userChallengeId);

    // 챌린지 리셋 (재시작)
    void resetChallenge(@Param("userChallengeId") Long userChallengeId,
                        @Param("startValue") Double startValue);

    // 실패한 챌린지 삭제
    void deleteUserChallenge(Long userChallengeId);

    // 유저에게 포인트 지급
    void addUserPoint(@Param("userId") Long userId, @Param("points") int points);

    // '성공 횟수 총합'을 구하는 메서드
    Integer sumTotalSuccessCount(Long userId);

    // Top50을 구하는 메서드
    List<LeaderboardEntryDto> findTop50Leaderboard();

    // 성공시 뱃지 지급
    void insertUserBadge(@Param("userId") Long userId, @Param("challengeId") Long challengeId);
}