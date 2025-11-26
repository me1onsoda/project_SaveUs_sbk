package com.example.Ex02.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.example.Ex02.dto.EmailVerificationTokenDto;
import com.example.Ex02.dto.UserJoinDto;
import com.example.Ex02.mapper.EmailVerificationTokenMapper;
import com.example.Ex02.mapper.UserMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailVerificationTokenMapper tokenMapper;

    @Autowired
    private MailService mailService;

    private final int TOKEN_EXPIRE_MINUTES = 30;


    @Transactional
    public void deleteUserAll(Long userId) {

        // 1. 자식 테이블 순서대로 삭제
        userMapper.deleteUserGoals(userId);
        userMapper.deleteUserHealthScores(userId);
        userMapper.deleteUserMeals(userId);
        tokenMapper.deleteTokensByUserId(userId);

        // 2. 마지막에 USERS 삭제
        userMapper.deleteUser(userId);
    }

    // 계정 인증 메일 전송
    public void sendJoinVerificationMail(String email, HttpSession session) {
        String token = makeToken();

        session.setAttribute("joinToken", token);
        session.setAttribute("joinEmail", email);
        session.setAttribute("joinExpireTime", System.currentTimeMillis() + (TOKEN_EXPIRE_MINUTES * 60 * 1000));

        mailService.sendVerificationCode(email, token);
    }

    // 계정 인증 토큰 검증(세션 기반)
    public boolean verifyJoinToken(String email, String inputToken, HttpSession session) {
        String sessionToken = (String) session.getAttribute("joinToken");
        String sessionEmail = (String) session.getAttribute("joinEmail");
        Long expireTime = (Long) session.getAttribute("joinExpireTime");

        if (sessionToken == null || sessionEmail == null || expireTime == null) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime > expireTime) {
            return false;
        }

        if (sessionEmail.equals(email) && sessionToken.equals(inputToken)) {
            session.setAttribute("verifiedEmail", email);
            return true;
        }

        return false;
    }

    // 비밀번호 변경 링크
    @Transactional
    public void sendPasswordResetMail(UserJoinDto user) {
        Long userId = user.getUserId();
        EmailVerificationTokenDto tokenDto = insertToken(userId);

        mailService.sendPasswordResetEmail(user.getEmail(), tokenDto.getToken());
    }

    // 비밀번호 변경 링크 검증
    @Transactional
    public Long consumePasswordResetMail(String token) {

        EmailVerificationTokenDto validToken = tokenMapper.findValidToken(token);
        if (validToken == null) {
            return null;
        }

        tokenMapper.deleteTokensByUserId(validToken.getUserId());
        return validToken.getUserId();
    }

    // 토큰 저장
    private EmailVerificationTokenDto insertToken(Long userId) {
        tokenMapper.deleteTokensByUserId(userId);

        String token = makeToken();
        LocalDateTime now = LocalDateTime.now();

        EmailVerificationTokenDto tokenDto = new EmailVerificationTokenDto();
        tokenDto.setUserId(userId);
        tokenDto.setToken(token);
        tokenDto.setCreatedAt(Timestamp.valueOf(now).toLocalDateTime());
        tokenDto.setExpiresAt(Timestamp.valueOf(now.plusMinutes(TOKEN_EXPIRE_MINUTES)).toLocalDateTime());

        tokenMapper.insertToken(tokenDto);

        return tokenDto;
    }

    // 토큰 생성
    private String makeToken() {
        SecureRandom random = new SecureRandom();
        char[] str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        int size = 12;

        return NanoIdUtils.randomNanoId(random, str, size);
    }
}
