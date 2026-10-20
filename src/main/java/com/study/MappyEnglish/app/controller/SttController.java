package com.study.MappyEnglish.app.controller;

import com.study.MappyEnglish.app.dto.SttResultDto;
import com.study.MappyEnglish.app.service.SttService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/stt")
@CrossOrigin(origins = "http://localhost:3000")
public class SttController {

    private final SttService sttService;

    public SttController(SttService sttService) {
        this.sttService = sttService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<SttResultDto> evaluateAudio(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam("targetText") String targetText) {

        try {
            SttResultDto result = sttService.evaluatePronunciation(audioFile, targetText);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/evaluate-text")
    public ResponseEntity<SttResultDto> evaluateText(
            @RequestParam("userText") String userText,
            @RequestParam("targetText") String targetText) {

        try {
            SttResultDto result = sttService.evaluateText(userText, targetText);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}