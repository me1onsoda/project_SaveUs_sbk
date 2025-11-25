package com.example.Ex02.scheduler;

import com.example.Ex02.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChallengeScheduler {

    private final ChallengeService challengeService;

    // 매일 자정(00:00:00) 실행
    // 초 분 시 일 월 요일
    @Scheduled(cron = "0 0 15 * * *")
    public void runDailyChallengeVerification() {
        log.info("=== [Nightly Batch] 챌린지 자동 검증 시작 ===");

        // 서비스의 검증 로직 호출
        challengeService.runDailyCheck();

        log.info("=== [Nightly Batch] 챌린지 자동 검증 종료 ===");
    }
}