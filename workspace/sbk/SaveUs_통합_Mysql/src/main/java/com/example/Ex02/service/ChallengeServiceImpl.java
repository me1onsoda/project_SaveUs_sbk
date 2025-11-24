package com.example.Ex02.service;

import com.example.Ex02.dto.*;
import com.example.Ex02.mapper.ChallengeMapper;
import com.example.Ex02.mapper.MealMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeMapper challengeMapper;
    private final AiFoodService aiFoodService;
    private final MealMapper mealMapper;

    @Override
    public MyChallengeSummaryDto getMyChallengeSummary(Long userId) {
        MyChallengeSummaryDto summary = new MyChallengeSummaryDto();
        if (userId == null || userId == 0) {
            summary.setActiveCount(0);
            summary.setCompletedCount(0);
            summary.setTotalPoints(0);
            return summary;
        }

        summary.setActiveCount(challengeMapper.countMyChallengesByStatus(userId, "ONGOING"));
        summary.setCompletedCount(challengeMapper.sumTotalSuccessCount(userId));

        Integer points = challengeMapper.getMyTotalPoints(userId);
        summary.setTotalPoints(points != null ? points : 0);

        return summary;
    }

    @Override
    public List<MyChallengeItemDto> findMyChallengesByStatus(Long userId, String status) {
        if (userId == null || userId == 0) {
            return List.of(); // 비로그인 시 빈 리스트
        }
        return challengeMapper.findMyChallengesByStatus(userId, status);
    }

    @Override
    public List<ChallengeCardDto> findAiRecommendedChallenges(Long userId) {
        String mainGoalStr = "HEALTH";
        if (userId != null && userId != 0) {
            Integer goalCode = challengeMapper.findUserMainGoal(userId);
            mainGoalStr = convertGoalCodeToString(goalCode);
        }

        return challengeMapper.findChallengesByGoal(mainGoalStr, userId);
    }

    @Override
    public List<TagDto> findAllTags() {
        return challengeMapper.findAllTags();
    }

    @Override
    public Map<String, Object> findAllChallengesPaginated(Long userId, String keyword, String tag, int page, int size) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("keyword", keyword);
        params.put("tag", tag);
        params.put("offset", (page - 1) * size);
        params.put("limit", size);

        List<ChallengeCardDto> challenges = challengeMapper.findAllChallengesPaginated(params);
        int totalCount = challengeMapper.countAllChallenges(params);

        Map<String, Object> result = new HashMap<>();
        result.put("challenges", challenges);
        result.put("totalCount", totalCount);
        result.put("currentPage", page);
        result.put("totalPages", (int) Math.ceil((double) totalCount / size));

        return result;
    }

    @Override
    public List<LeaderboardEntryDto> findLeaderboardTop5() {
        return challengeMapper.findLeaderboardTop5();
    }

    @Override
    @Transactional
    public void joinChallenge(Long userId, Long challengeId) {

        // 어떤 챌린지인지 정보 가져오기
        ChallengeCardDto challenge = challengeMapper.findChallengeDetail(challengeId);

        Double startValue = null;

        // 만약 'CHECK' 타입(변화량 측정)이고, 키워드가 체중 관련이면?
        // (metricKey가 'weight_loss', 'weight_maintain', 'weight_goal' 등인 경우)
        if ("CHECK".equals(challenge.getChallengeType()) &&
                challenge.getMetricKey().startsWith("weight")) {

            // 유저의 현재 몸무게를 가져와서 '시작 값'으로
            Double currentWeight = challengeMapper.getUserWeight(userId);
            if (currentWeight != null) {
                startValue = currentWeight;
            }
        }

        // DB에 저장 (시작 몸무게 포함)
        challengeMapper.joinChallenge(userId, challengeId, startValue);
    }

   /* @Override
    @Transactional
    public void runDailyCheck() {
        // '진행중(ONGOING)'인 모든 챌린지 가져오기
        List<MyChallengeItemDto> activeChallenges = challengeMapper.findAllActiveChallenges();

        for (MyChallengeItemDto uc : activeChallenges) {
            boolean isSuccess = false;

            // 유저의 오늘 영양 섭취량 가져오기 (NutritionMapper 필요)
            // DailyNutritionDto dailyLog = nutritionMapper.getDailySummary(uc.getUserId(), LocalDate.now().minusDays(1));
            // (테스트용 데이터: 영양섭취량 개발 완료 시 위 주석 풀고 연동)
            Map<String, Double> dailyLog = new HashMap<>();
            dailyLog.put("sodium", 1800.0); // ex) 어제 나트륨 1800 섭취
            dailyLog.put("protein", 70.0);  // ex) 어제 단백질 70 섭취

            // 챌린지 타입별로 채점
            switch (uc.getChallengeType()) {
                case "LIMIT": // "이하"여야 성공 (나트륨, 당류 등)
                    double consumedLimit = dailyLog.getOrDefault(uc.getMetricKey(), 0.0);
                    if (consumedLimit <= uc.getTargetValue()) {
                        isSuccess = true;
                    }
                    break;

                case "MINIMUM": // "이상"이어야 성공 (단백질, 식이섬유 등)
                    double consumedMin = dailyLog.getOrDefault(uc.getMetricKey(), 0.0);
                    if (consumedMin >= uc.getTargetValue()) {
                        isSuccess = true;
                    }
                    break;

                case "CHECK": // "변화량" 체크 (체중 감량)
                    // 현재 체중 가져오기
                    Double currentWeight = challengeMapper.getUserWeight(uc.getUserId()); // 매퍼에 추가 필요
                    if (currentWeight != null && uc.getStartValue() != null) {
                        // (시작 체중 - 현재 체중) >= 목표 감량치
                        // 예: 목표가 -1kg. (70 - 69) = 1kg 감량.
                        // TargetValue를 (1kg 감량)로 가정
                        // 현재 체중 <= (시작 체중 + 목표변화량)
                        // 예: 69 <= (70 + (-1)) -> 69 <= 69 (성공)
                        if (currentWeight <= (uc.getStartValue() + uc.getTargetValue())) {
                            isSuccess = true;
                        }
                    }
                    break;
            }

            // 성공 시 업데이트
            if (isSuccess) {
                // DB 카운트 증가 (+1)
                challengeMapper.increaseCurrentCount(uc.getUserChallengeId());

                // ★ 최종 완료 체크 및 포인트 지급
                if (uc.getCurrentCount() + 1 >= uc.getDurationDays()) {
                    // 상태 완료 변경
                    challengeMapper.completeChallenge(uc.getUserChallengeId());

                    // 포인트 지급 (Challenges 테이블에 있는 점수만큼)
                    if (uc.getPoints() != null && uc.getPoints() > 0) {
                        challengeMapper.addUserPoint(uc.getUserId(), uc.getPoints());
                    }
                }
            } else {
                //  실패: 오늘 목표 달성 실패 시 즉시 'FAILED' 처리
                challengeMapper.failChallenge(uc.getUserChallengeId());
            }
        }
    }*/

    // 몇몇 유저의 챌린지 검증에서 오류가 발생하더라도 다른 유저의 검증은 정상적으로 처리되도록 분리
    // 하지만 유저 하나하나를 순서대로 검증하기때문에 시간소요
    @Override
    public void runDailyCheck() {
        // 진행 중인 모든 챌린지 가져오기
        List<MyChallengeItemDto> activeChallenges = challengeMapper.findAllActiveChallenges();

        for (MyChallengeItemDto uc : activeChallenges) {
            try {
                //한 명씩 따로따로 //오류발생시 로그만 남기고 다음 유저 처리하러
                verifySingleChallenge(uc);
            } catch (Exception e) {
                System.out.println("에러 발생 (ID: " + uc.getUserChallengeId() + ") - " + e.getMessage());
            }
        }
    }

    // 한 건만 처리
    @Transactional
    public void verifySingleChallenge(MyChallengeItemDto uc) {
        boolean isSuccess = false;

        // 유저의 전날 영양 섭취량 가져오기
        MealDto dailyLog = mealMapper.findYesterdayTotalNutrition(uc.getUserId());

        // null 처리 (기록이 없어도 0으로 처리)
        if (dailyLog == null) {
            dailyLog = new MealDto();
        }
        System.out.println(dailyLog.toString());

        // 챌린지 타입별로 채점
        switch (uc.getChallengeType()) {
            case "LIMIT": // "이하"여야 성공 (나트륨, 당류 등)
                double consumedLimit = getValueByMetricKey(dailyLog, uc.getMetricKey());
                if (consumedLimit <= uc.getTargetValue()) {
                    isSuccess = true;
                }
                break;

            case "MINIMUM": // "이상"이어야 성공 (단백질, 식이섬유 등)
                double consumedMin = getValueByMetricKey(dailyLog, uc.getMetricKey());
                if (consumedMin >= uc.getTargetValue()) {
                    isSuccess = true;
                }
                break;

            case "CHECK": // "변화량" 체크 (체중 감량)
                // 현재 체중 가져오기
                Double currentWeight = challengeMapper.getUserWeight(uc.getUserId());
                if (currentWeight != null && uc.getStartValue() != null) {
                    // 예: 현재 69 <= (시작 70 + 목표 -1) -> 성공
                    if (currentWeight <= (uc.getStartValue() + uc.getTargetValue())) {
                        isSuccess = true;
                    }
                }
                break;
        }

        // 성공 시 업데이트
        if (isSuccess) {

            int newCount = uc.getCurrentCount() + 1;
            int duration = uc.getDurationDays() > 0 ? uc.getDurationDays() : 1; // 0 나누기 방지

            // 퍼센트 계산 ( (현재 / 목표) * 100 )
            int newPercent = (int) ((double) newCount / duration * 100);
            if (newPercent > 100) newPercent = 100; // 100% 넘지 않게 막음

            // DB에 카운트와 퍼센트 동시 반영
            challengeMapper.updateProgress(uc.getUserChallengeId(), newCount, newPercent);
            // 최종 완료 체크
            if (newCount >= duration) {
                challengeMapper.completeChallenge(uc.getUserChallengeId());
                // 포인트 지급
                if (uc.getPoints() != null && uc.getPoints() > 0) {
                    challengeMapper.addUserPoint(uc.getUserId(), uc.getPoints());
                }
                // 뱃지 지급 (중복 시 쿼리에서 무시됨)
                challengeMapper.insertUserBadge(uc.getUserId(), uc.getChallengeId());
            }
        } else {
            // 실패 처리 (
            challengeMapper.failChallenge(uc.getUserChallengeId());
        }

        //당뇨 점수 계산
        List<DiabetesScoreDto> dtoList = mealMapper.selectYesterdayNutritionForAllUsers();

        // 2. 파이썬 서버에 계산 요청
        List<DiabetesScoreDto> results = aiFoodService.calculateDietScores(dtoList);

        // 3. DB 저장
        for (DiabetesScoreDto res : results) {
            if (res.getError() != null) continue;
            mealMapper.insertDiabetesScore(res); // 이름 바뀐 메서드 호출
        }
    }

    // MealDto의 Integer 값을 double로 안전하게 변환하여 리턴
    private double getValueByMetricKey(MealDto dto, String key) {
        if (key == null) return 0.0;

        // key는 소문자로 비교
        switch (key.toLowerCase()) {
            case "calories": return dto.getCalories() != null ? dto.getCalories() : 0.0;
            case "protein":  return dto.getProtein()  != null ? dto.getProtein()  : 0.0;
            case "carbs":    return dto.getCarbs()    != null ? dto.getCarbs()    : 0.0;
            case "fat":      return dto.getFat()      != null ? dto.getFat()      : 0.0;
            case "sugar":    return dto.getSugar()    != null ? dto.getSugar()    : 0.0;
            case "sodium":   return dto.getSodium()   != null ? dto.getSodium()   : 0.0;
            case "fiber":    return dto.getFiber()    != null ? dto.getFiber()    : 0.0;
            case "calcium":  return dto.getCalcium()  != null ? dto.getCalcium()  : 0.0;
            default: return 0.0;
        }
    }


    private String convertGoalCodeToString(Integer code) {
        if (code == null) return "HEALTH"; // null이면 기본값

        return switch (code) {
            case 0 -> "WEIGHT_LOSS";       // 1번: 감량
            case 1 -> "WEIGHT_GAIN";  // 2번: 중량
            case 2 -> "HEALTH";  // 3번: 건강식
            // case 4: return "MUSCLE_GAIN"; // 필요시 추가
            default -> "HEALTH";      // 그 외는 기본값
        };
    }

    @Override
    public void restartChallenge(Long userChallengeId) {
        // 해당 챌린지를 찾아서 초기화 (STATUS='ONGOING', COUNT=0, START_DATE=오늘, START_VALUE=갱신)
        // 기존 기록 가져오기 (어떤 챌린지인지 알기 위해)
        MyChallengeItemDto uc = challengeMapper.findUserChallengeById(userChallengeId);

        // 체중 챌린지라면 '시작 몸무게'도 현재 기준으로 다시 잡아야 함
        Double newStartValue = null;
        if ("CHECK".equals(uc.getChallengeType())) {
            newStartValue = challengeMapper.getUserWeight(uc.getUserId());
        }

        // DB 업데이트 (재시작)
        challengeMapper.resetChallenge(userChallengeId, newStartValue);
    }

    // 삭제 메서드
    @Override
    public void deleteChallenge(Long userChallengeId) {
        challengeMapper.deleteUserChallenge(userChallengeId);
    }

    //Top50 메서드
    @Override
    public List<LeaderboardEntryDto> getTop50Leaderboard() {
        return challengeMapper.findTop50Leaderboard();
    }
}