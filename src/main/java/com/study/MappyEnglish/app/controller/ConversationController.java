package com.study.MappyEnglish.app.controller;

import com.study.MappyEnglish.app.dto.ConversationDto;
import com.study.MappyEnglish.app.entity.Conversation;
import com.study.MappyEnglish.app.service.ConversationService;
import com.study.MappyEnglish.app.dto.SearchResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.study.MappyEnglish.app.dto.ConversationCreateDto;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService service;

    public ConversationController(ConversationService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/place/{placeId}")
    public ResponseEntity<List<ConversationDto>> listByPlace(@PathVariable Long placeId,
                                                             @RequestParam(defaultValue = "true") boolean includePlace) {
        return ResponseEntity.ok(service.listByPlace(placeId, includePlace));
    }

    // 카테고리별 조회 API
    // GET /api/conversations/section?code=A
    @GetMapping("/section")
    public ResponseEntity<List<SearchResponseDto.ConversationDto>> getByCategory(@RequestParam String code) {
        List<Conversation> conversations = service.getConversationsBySection(code);
        // DTO 변환
        List<SearchResponseDto.ConversationDto> dtos = conversations.stream()
                .map(SearchResponseDto.ConversationDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // AI 대화 생성 데이터를 DB에 저장하는 POST API
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')") // 관리자 전용 API
    public ResponseEntity<String> createConversation(@RequestBody ConversationCreateDto request) {
        service.saveConversation(request); // 서비스로 데이터 넘기기
        return ResponseEntity.ok("대화가 성공적으로 저장되었습니다!");
    }
}