package com.example.Ex02.mapper;

import com.example.Ex02.dto.EmailVerificationTokenDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailVerificationTokenMapper {

    void insertToken(EmailVerificationTokenDto dto);

    EmailVerificationTokenDto findValidToken(String token);

    EmailVerificationTokenDto findLatestByUserId(Long userId);

    void deleteTokensByUserId(Long userId);
}
