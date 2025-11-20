package com.example.Ex02.controller;

import com.example.Ex02.dto.LeaderboardEntryDto;
import com.example.Ex02.service.ChallengeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChallengesController {

    private final ChallengeService challengeService;

    @GetMapping("/challenges")
    public String challenges(Model model, HttpSession session,
                             @RequestParam(value = "keyword", required = false) String keyword,
                             @RequestParam(value = "tag", required = false) String tag,
                             @RequestParam(value = "page", defaultValue = "1") int page) {

        // 사용자 ID 가져오기
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            userId = 0L; // 비로그인 사용자는 0L로 처리
        }


        // "나의 챌린지" 탭 데이터 (기본 탭)
        model.addAttribute("summary", challengeService.getMyChallengeSummary(userId));
        model.addAttribute("ongoingChallenges", challengeService.findMyChallengesByStatus(userId, "ONGOING"));
        model.addAttribute("completedChallenges", challengeService.findMyChallengesByStatus(userId, "COMPLETED"));

        // "둘러보기" 탭 데이터
        model.addAttribute("aiRecommendedChallenges", challengeService.findAiRecommendedChallenges(userId));
        model.addAttribute("allTags", challengeService.findAllTags());

        // "둘러보기" - 페이징된 전체 챌린지 목록
        int size = 5; // 한 페이지에 5개씩
        Map<String, Object> challengePageData = challengeService.findAllChallengesPaginated(userId, keyword, tag, page, size);
        model.addAttribute("challengePage", challengePageData);
        model.addAttribute("currentKeyword", keyword); // 검색어 유지를 위해
        model.addAttribute("currentTag", tag); // 태그 활성화 유지를 위해

        // "사이드바" 랭킹 데이터
        model.addAttribute("leaderboard", challengeService.findLeaderboardTop5());

        // 챌린지 실패시 초기화
        model.addAttribute("failedChallenges", challengeService.findMyChallengesByStatus(userId, "FAILED"));
        return "challenges";
    }

    // 재시작 버튼 클릭 시
    @PostMapping("/challenges/{userChallengeId}/restart")
    public String restartChallenge(@PathVariable Long userChallengeId) {
        challengeService.restartChallenge(userChallengeId);
        return "redirect:/challenges?tab=my-challenges-tab";
    }

    // 실패한 챌린지 삭제 (포기/목록에서 제거)
    @PostMapping("/challenges/{userChallengeId}/delete")
    public String deleteChallenge(@PathVariable Long userChallengeId) {
        challengeService.deleteChallenge(userChallengeId);
        return "redirect:/challenges?tab=my-challenges-tab";
    }

    @PostMapping("/challenges/{challengeId}/join")
    public String joinChallenge(
            @PathVariable Long challengeId,
            @SessionAttribute(name = "userId", required = false) Long userId
    ) {
        // 로그인 체크 (없으면 로그인 페이지로 튕겨내기)
        if (userId == null) {
            return "redirect:/user/login";
        }

        // 서비스 호출 (DB 저장)
        challengeService.joinChallenge(userId, challengeId);

        // 완료 후 '나의 챌린지' 탭으로 이동 (tab 파라미터 사용)
        return "redirect:/challenges?tab=my-challenges-tab";
    }

    @GetMapping("/api/leaderboard")
    @ResponseBody // HTML이 아니라 데이터(JSON)를 줌
    public List<LeaderboardEntryDto> getLeaderboardAPI() {
        return challengeService.getTop50Leaderboard();
    }


    // *************** 테스트용 강제 스케줄러 실행 (브라우저에서 호출) ***************
    @GetMapping("/test/run-scheduler")
    @ResponseBody // 화면 없이 글자만 리턴
    public String testScheduler() {
        System.out.println(">>> [TEST] 강제 스케줄러 실행 요청");
        challengeService.runDailyCheck();
        return "스케줄러 로직 실행 완료.";
    }
}