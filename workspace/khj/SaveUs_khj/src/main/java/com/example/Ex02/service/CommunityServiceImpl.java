package com.example.Ex02.service;

import com.example.Ex02.dto.CommentDto;
import com.example.Ex02.dto.CommunityPostDto;
import com.example.Ex02.dto.TrendingPostDto;
import com.example.Ex02.mapper.CommunityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp; // Timestamp import
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    // 임시 현재 로그인 사용자 ID (나중에 Security Context에서 가져오도록 변경)
    private final Long TEMP_CURRENT_USER_ID = 1L;

    private final CommunityMapper communityMapper;

    @Override
    public List<CommunityPostDto> getPostList() {

        List<CommunityPostDto> postList = communityMapper.selectPostList(TEMP_CURRENT_USER_ID);

        for (CommunityPostDto post : postList) {
            List<String> imageUrls = communityMapper.selectImagesByPostId(post.getPostId());
            post.setImageUrls(imageUrls);

            String timeAgo = formatTimeAgo(post.getCreatedAt()); // 원본 시간으로 계산
            post.setTimeAgo(timeAgo); // 문자열(...분 전)로 덮어쓰기
        }

        return postList;
    }

    @Override
    public List<CommentDto> getCommentsByPostId(long postId) { //상세보기에 필요한 데이터
        List<CommentDto> commentList = communityMapper.selectCommentsByPostId(postId);

        for (CommentDto comment : commentList) {
            // TimeAgo 계산
            String timeAgo = formatTimeAgo(comment.getCreatedAt());
            comment.setTimeAgo(timeAgo);
        }

        return commentList;
    }

    @Override
    public List<TrendingPostDto> getTrendingList() {
        return communityMapper.selectTrendingList();
    }

    private String formatTimeAgo(Timestamp createdAt) {
        if (createdAt == null) {
            return "";
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = createdAt.toLocalDateTime();
        Duration duration = Duration.between(past, now);
        long minutes = duration.toMinutes();
        if (minutes < 60) {
            return minutes + " minutes ago";
        }
        long hours = duration.toHours();
        if (hours < 24) {
            return hours + " hours ago";
        }
        long days = duration.toDays();
        if (days < 30) {
            return days + " days ago";
        }
        // 30일 이상은 간단히 "YYYY.MM.DD" 형태로 반환 (선택 사항)
        return past.toLocalDate().toString().replace('-', '.');
    }
}