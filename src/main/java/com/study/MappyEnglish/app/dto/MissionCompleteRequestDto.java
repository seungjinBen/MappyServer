package com.study.MappyEnglish.app.dto;

// 프론트 -> 백엔드: 미션 완료 신호
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // JSON 파싱용
@AllArgsConstructor // DB에서 데이터 뽑아올 때 쓸 생성자 추가
public class MissionCompleteRequestDto {
    private Long placeId;
    private Long conversationId;
}