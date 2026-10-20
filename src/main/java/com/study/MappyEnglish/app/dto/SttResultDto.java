package com.study.MappyEnglish.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SttResultDto {
    private String recognizedText; // Whisper가 알아들은 유저의 음성 텍스트
    private String targetText;     // 원래 말했어야 하는 정답 텍스트
    private int score;             // 발음 유사도 점수 (0 ~ 100점)
    String feedback;
}