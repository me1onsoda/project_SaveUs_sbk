/*
package com.example.Ex02;

import com.example.Ex02.dto.CommentDto;
import com.example.Ex02.dto.CommunityPostDto;
import com.example.Ex02.service.CommunityService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable("postId") long postId,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Login required"));
        }
        try {
            int newLikeCount = communityService.toggleLike(postId, userId);
            return ResponseEntity.ok(Map.of("success", true, "newLikeCount", newLikeCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    @PostMapping("/posts/{postId}/comment")
    public ResponseEntity<Map<String, Object>> createComment(
            @PathVariable("postId") long postId,
            @RequestBody Map<String, String> payload,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Login required"));
        }
        try {
            String content = payload.get("content");
            CommentDto newComment = communityService.createComment(postId, content, userId);
            return ResponseEntity.ok(Map.of("success", true, "newComment", newComment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }


    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Map<String, Object>> deletePost(
            @PathVariable("postId") long postId,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Login required"));
        }
        try {
            communityService.deletePost(postId, userId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            // AccessDeniedException (403) 또는 RuntimeException (400)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<Map<String, Object>> updatePost(
            @PathVariable("postId") long postId,
            @RequestParam("content") String content,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "imagesToDelete", required = false) List<String> imagesToDelete,
            HttpSession session) throws IOException {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Login required"));
        }
        try {
            // 서비스 호출 시 imagesToDelete 전달
            CommunityPostDto updatedPost = communityService.updatePost(postId, userId, content, images, imagesToDelete);
            return ResponseEntity.ok(Map.of("success", true, "updatedPost", updatedPost));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/posts/popular")
    public ResponseEntity<List<CommunityPostDto>> getPopularPosts(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        // 비로그인 사용자(userId=null)도 조회는 가능하도록

        try {
            List<CommunityPostDto> popularList = communityService.getPopularPostList(userId);
            return ResponseEntity.ok(popularList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 3. 댓글 삭제 API
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> deleteComment(
            @PathVariable("commentId") long commentId,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Login required"));
        }
        try {
            communityService.deleteComment(commentId, userId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> updateComment(
            @PathVariable("commentId") long commentId,
            @RequestBody Map<String, String> payload, // { "content": "..." }
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Login required"));
        }
        try {
            String content = payload.get("content");
            CommentDto updatedComment = communityService.updateComment(commentId, userId, content);
            return ResponseEntity.ok(Map.of("success", true, "updatedComment", updatedComment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
*/
package com.example.Ex02.controller; // (패키지명은 기존 파일과 동일하게 유지하세요)

import com.example.Ex02.dto.CommentDto;
import com.example.Ex02.dto.CommunityPostDto;
import com.example.Ex02.service.CommunityService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommunityApiController {

    private final CommunityService communityService;

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsForPost(@PathVariable("postId") long postId) {
        try {
            List<CommentDto> comments = communityService.getCommentsByPostId(postId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable("postId") long postId,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Login required"));
        }
        try {
            int newLikeCount = communityService.toggleLike(postId, userId);
            return ResponseEntity.ok(Map.of("success", true, "newLikeCount", newLikeCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    @PostMapping("/posts/{postId}/comment")
    public ResponseEntity<Map<String, Object>> createComment(
            @PathVariable("postId") long postId,
            @RequestBody Map<String, String> payload,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Login required"));
        }
        try {
            String content = payload.get("content");
            CommentDto newComment = communityService.createComment(postId, content, userId);
            return ResponseEntity.ok(Map.of("success", true, "newComment", newComment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }


    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Map<String, Object>> deletePost(
            @PathVariable("postId") long postId,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Login required"));
        }
        try {
            communityService.deletePost(postId, userId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<Map<String, Object>> updatePost(
            @PathVariable("postId") long postId,
            @RequestParam("content") String content,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "imagesToDelete", required = false) List<String> imagesToDelete,
            HttpSession session) throws IOException {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Login required"));
        }
        try {
            CommunityPostDto updatedPost = communityService.updatePost(postId, userId, content, images, imagesToDelete);
            return ResponseEntity.ok(Map.of("success", true, "updatedPost", updatedPost));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/posts/popular")
    public ResponseEntity<List<CommunityPostDto>> getPopularPosts(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        try {
            List<CommunityPostDto> popularList = communityService.getPopularPostList(userId);
            return ResponseEntity.ok(popularList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> deleteComment(
            @PathVariable("commentId") long commentId,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Login required"));
        }
        try {
            communityService.deleteComment(commentId, userId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> updateComment(
            @PathVariable("commentId") long commentId,
            @RequestBody Map<String, String> payload, // { "content": "..." }
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Login required"));
        }
        try {
            String content = payload.get("content");
            CommentDto updatedComment = communityService.updateComment(commentId, userId, content);
            return ResponseEntity.ok(Map.of("success", true, "updatedComment", updatedComment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}