package com.study.MappyEnglish.app.dto;

import com.study.MappyEnglish.app.entity.SavedConversation;
import lombok.Getter;

@Getter
public class SavedConversationResponseDto {
    private Long savedId;
    private Long conversationId;
    private String placeName;
    private String type;

    private String englishText1;
    private String koreanText1;
    private String audioUrl1;

    private String englishText2;
    private String koreanText2;
    private String audioUrl2;

    private String englishText3;
    private String koreanText3;
    private String audioUrl3;

    private String englishText4;
    private String koreanText4;
    private String audioUrl4;

    private String englishText5;
    private String koreanText5;
    private String audioUrl5;

    public SavedConversationResponseDto(SavedConversation entity) {
        this.savedId = entity.getId();
        this.conversationId = entity.getConversation().getId();
        this.placeName = entity.getConversation().getPlace().getName();
        this.type = String.valueOf(entity.getConversation().getType());

        // 1번
        this.englishText1 = entity.getConversation().getEnglishText1();
        this.koreanText1 = entity.getConversation().getKoreanText1();
        this.audioUrl1 = entity.getConversation().getAudioUrl1();

        // 2~5번 매핑
        this.englishText2 = entity.getConversation().getEnglishText2();
        this.koreanText2 = entity.getConversation().getKoreanText2();
        this.audioUrl2 = entity.getConversation().getAudioUrl2();

        this.englishText3 = entity.getConversation().getEnglishText3();
        this.koreanText3 = entity.getConversation().getKoreanText3();
        this.audioUrl3 = entity.getConversation().getAudioUrl3();

        this.englishText4 = entity.getConversation().getEnglishText4();
        this.koreanText4 = entity.getConversation().getKoreanText4();
        this.audioUrl4 = entity.getConversation().getAudioUrl4();

        this.englishText5 = entity.getConversation().getEnglishText5();
        this.koreanText5 = entity.getConversation().getKoreanText5();
        this.audioUrl5 = entity.getConversation().getAudioUrl5();
    }
}