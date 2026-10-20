package com.study.MappyEnglish.app.dto;
// 백엔드 -> 프론트: 내 진도율 성적표

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MissionProgressResponseDto {
    private Long placeId;
    private Long completedCount; // 해당 장소에서 완료한 대화 개수
}
