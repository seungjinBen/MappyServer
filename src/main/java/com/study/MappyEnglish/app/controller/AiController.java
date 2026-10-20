package com.study.MappyEnglish.app.controller;

import com.study.MappyEnglish.app.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    // 프론트엔드에서 장소 이름과 타입을 주면 AI가 만든 JSON 문자열을 반환
    @PostMapping("/generate")
    public ResponseEntity<String> generateConversation(@RequestBody Map<String, String> request) {
        String placeName = request.get("placeName");
        String type = request.get("type"); // "A" or "B"

        // AI 서비스 호출
        String jsonResult = aiService.generateDialogue(placeName, type);

        // 프론트엔드로 그대로 전달 (프론트엔드는 이 JSON 문자열을 파싱해서 화면에 띄움)
        return ResponseEntity.ok(jsonResult);
    }
}