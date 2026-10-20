package com.study.MappyEnglish.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    // 클라우드 서버나 로드밸런서가 서버 생존 여부를 확인할 때 쓰는 API
    @GetMapping("/api/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong!");
    }
}