package com.study.MappyEnglish.app.controller;

import com.study.MappyEnglish.app.dto.SavedConversationResponseDto;
import com.study.MappyEnglish.app.service.SavedConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class SavedConversationController {

    private final SavedConversationService savedService;

    @PostMapping
    public ResponseEntity<String> addBookmark(
            Authentication authentication, // 토큰 객체
            @RequestParam Long conversationId) {

        // 수정됨: 억지로 숫자로 바꾸지 않고 이메일(String) 그대로 가져옴
        String email = authentication.getName();

        savedService.saveConversation(email, conversationId);
        return ResponseEntity.ok("대화가 저장되었습니다.");
    }

    @GetMapping("/my")
    public ResponseEntity<List<SavedConversationResponseDto>> getBookmarks(Authentication authentication) {
        String email = authentication.getName(); // 수정됨
        List<SavedConversationResponseDto> list = savedService.getMySavedConversations(email);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping
    public ResponseEntity<String> removeBookmark(
            Authentication authentication,
            @RequestParam Long conversationId) {

        String email = authentication.getName(); // 수정됨
        savedService.deleteSavedConversation(email, conversationId);
        return ResponseEntity.ok("저장이 취소되었습니다.");
    }
}