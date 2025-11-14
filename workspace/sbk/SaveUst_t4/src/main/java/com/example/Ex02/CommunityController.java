package com.example.Ex02;

import com.example.Ex02.dto.CommentDto;
import com.example.Ex02.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable("postId") long postId) {
        try {
            int newLikeCount = communityService.toggleLike(postId);
            return ResponseEntity.ok(Map.of("success", true, "newLikeCount", newLikeCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/posts/{postId}/comment")
    public ResponseEntity<Map<String, Object>> createComment(
            @PathVariable("postId") long postId,
            @RequestBody Map<String, String> payload) { // JSON { "content": "..." }
        try {
            String content = payload.get("content");
            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Content is empty"));
            }
            CommentDto newComment = communityService.createComment(postId, content);
            return ResponseEntity.ok(Map.of("success", true, "newComment", newComment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}