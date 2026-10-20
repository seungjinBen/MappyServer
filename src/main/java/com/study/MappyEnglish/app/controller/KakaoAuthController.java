package com.study.MappyEnglish.app.controller;

import com.study.MappyEnglish.app.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    // 프론트에서 받은 코드를 통과시켜 JWT를 반환!
    @PostMapping("/kakao")
    public ResponseEntity<Map<String, String>> kakaoLogin(@RequestBody Map<String, String> request) {
        String code = request.get("code");

        // 서비스 로직 실행 후 우리 서버의 JWT 토큰 반환
        String jwtToken = kakaoAuthService.kakaoLogin(code);

        Map<String, String> response = new HashMap<>();
        response.put("token", jwtToken);

        return ResponseEntity.ok(response);
    }
}