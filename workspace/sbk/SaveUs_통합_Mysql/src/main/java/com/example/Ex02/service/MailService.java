package com.example.Ex02.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}:${server.port}")
    private String baseUrl;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // 이메일 인증용 코드
    public void sendVerificationCode(String to, String token) {
        String subject = "[saveus] 회원가입 인증 코드 안내";

        String html = "<div style='font-family:Arial,sans-serif; text-align:center; border:1px solid #e0e0e0; padding:20px; max-width:400px; margin:0 auto; border-radius:8px;'>"
                + "<h2 style='color:#333; margin-bottom:10px;'>이메일 인증</h2>"
                + "<p style='color:#666; font-size:14px; margin-bottom:30px;'>아래 인증 코드 12자리를 회원가입 화면에 입력해주세요.</p>"
                + "<div style='margin: 20px 0;'>"
                + "<span style='display:inline-block; padding:15px 25px; background:#f2f4f8; color:#333; "
                + "font-size:32px; font-weight:bold; letter-spacing:8px; border:2px dashed #2f6fed; border-radius:8px; user-select:all;'>"
                + token
                + "</span>"
                + "</div>"
                + "<p style='color:#999; font-size:12px; margin-top:30px;'>인증 코드는 30분간 유효합니다.</p>"
                + "</div>";

        sendHtml(to, subject, html);
    }

    // 비밀번호 변경용 링크
    public void sendPasswordResetEmail(String to, String token) {
        String link = baseUrl + "/user/reset-pw?token=" + token;

        String subject = "[saveus] 비밀번호 재설정 안내";
        String html = "<div style='font-family:Arial,sans-serif; text-align:center; border:1px solid #e0e0e0; padding:20px; max-width:400px; margin:0 auto; border-radius:8px;'>"
                + "<h2 style='color:#333;'>비밀번호 재설정</h2>"
                + "<p style='color:#666; margin-bottom:20px;'>아래 버튼을 클릭하여 비밀번호를 재설정하세요.</p>"
                + "<a href='" + link + "' "
                + "style='display:inline-block; padding:12px 24px; background:#2f6fed; color:#fff; "
                + "text-decoration:none; font-weight:bold; border-radius:6px; font-size:16px;'>비밀번호 변경하기</a>"
                + "<p style='color:#999; font-size:12px; margin-top:30px;'>링크는 일정 시간이 지나면 만료됩니다.</p>"
                + "</div>";

        sendHtml(to, subject, html);
    }

    // 공통 메일 전송 로직
    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new IllegalStateException("메일 전송 실패: " + e.getMessage(), e);
        }
    }
}
