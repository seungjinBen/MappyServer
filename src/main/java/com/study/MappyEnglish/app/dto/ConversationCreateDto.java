package com.study.MappyEnglish.app.dto;

import lombok.Data;
import java.util.List;

@Data
public class ConversationCreateDto {
    private Long placeId;
    private String type;
    private Integer step; // 필요하다면 사용
    private String category;
    private List<LineDto> lines;

    @Data
    public static class LineDto {
        private Integer lineOrder;
        private String englishText;
        private String koreanText;
        private String audioUrl;
    }
}
