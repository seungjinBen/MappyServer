package com.study.MappyEnglish.app.dto;

import com.study.MappyEnglish.app.entity.Conversation;
import com.study.MappyEnglish.app.entity.Place;

import java.util.ArrayList;
import java.util.List;

// ConversationMapper.java
public class ConversationMapper {

    public static ConversationDto toDto(Conversation e) {
        if (e == null) return null;

        ConversationDto dto = new ConversationDto();
        dto.setId(e.getId());
        dto.setType(e.getType());
        dto.setCategory(e.getCategory());

        Place p = e.getPlace(); // LAZY - 접근 시 쿼리 발생 (fetch join으로 최적화 가능)
        if (p != null) {
            dto.setPlace(new ConversationDto.PlaceBriefDto(
                    p.getId(), p.getName(), p.getCategory(), p.getLat(), p.getLng()
            ));
        }

        addIfPresent(dto, 1, e.getEnglishText1(), e.getKoreanText1(), e.getAudioUrl1());
        addIfPresent(dto, 2, e.getEnglishText2(), e.getKoreanText2(), e.getAudioUrl2());
        addIfPresent(dto, 3, e.getEnglishText3(), e.getKoreanText3(), e.getAudioUrl3());
        addIfPresent(dto, 4, e.getEnglishText4(), e.getKoreanText4(), e.getAudioUrl4());
        addIfPresent(dto, 5, e.getEnglishText5(), e.getKoreanText5(), e.getAudioUrl5());

        return dto;
    }

    private static void addIfPresent(ConversationDto dto, int order,
                                     String eng, String kor, String audio) {
        if (isBlank(eng) && isBlank(kor) && isBlank(audio)) return;
        dto.addLine(new ConversationDto.LineDto(order,
                nullToEmpty(eng), nullToEmpty(kor), nullToEmpty(audio)));
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    private static String nullToEmpty(String s) {
        return (s == null) ? "" : s;
    }
}