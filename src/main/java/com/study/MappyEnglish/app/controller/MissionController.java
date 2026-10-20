package com.study.MappyEnglish.app.controller;

import com.study.MappyEnglish.app.dto.MissionCompleteRequestDto;
import com.study.MappyEnglish.app.dto.MissionProgressResponseDto;
import com.study.MappyEnglish.app.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    // 1. 미션 완료 시 프론트에서 쏘는 API
    @PostMapping("/complete")
    public ResponseEntity<String> completeMission(
            Authentication authentication, // 토큰에서 정보 가져오기
            @RequestBody MissionCompleteRequestDto requestDto) {

        // 이메일 추출
        String email = authentication.getName();

        missionService.completeMission(email, requestDto); // 이메일을 서비스로 넘김
        return ResponseEntity.ok("Mission Completed!");
    }

    // 2. 바텀 시트 열 때 내 진도율 가져오는 API
    @GetMapping("/progress/my")
    public ResponseEntity<List<MissionProgressResponseDto>> getMyProgress(
            Authentication authentication) {

        // 이메일 추출
        String email = authentication.getName();

        List<MissionProgressResponseDto> progress = missionService.getMyProgress(email);
        return ResponseEntity.ok(progress);
    }
    @GetMapping("/progress/details")
    public ResponseEntity<List<MissionCompleteRequestDto>> getCompletedDetails(Authentication authentication) {
        String email = authentication.getName();
        List<MissionCompleteRequestDto> details = missionService.getCompletedDetails(email);
        return ResponseEntity.ok(details);
    }
}