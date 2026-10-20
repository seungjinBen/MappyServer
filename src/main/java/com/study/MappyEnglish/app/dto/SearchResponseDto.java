package com.study.MappyEnglish.app.dto;

import com.study.MappyEnglish.app.entity.Conversation;
import com.study.MappyEnglish.app.entity.Place;
import lombok.Getter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SearchResponseDto {
    private List<PlaceDto> places;
    private List<ConversationDto> conversations;

    public SearchResponseDto(List<Place> placeList, List<Conversation> conversationList) {
        this.places = placeList.stream().map(PlaceDto::new).collect(Collectors.toList());
        this.conversations = conversationList.stream().map(ConversationDto::new).collect(Collectors.toList());
    }

    // 내부 클래스로 간단한 DTO 정의 (필요한 정보만)
    @Getter
    public static class PlaceDto {
        private Long id;
        private String name;
        private String description;

        public PlaceDto(Place place) {
            this.id = place.getId();
            this.name = place.getName();
            this.description = place.getDescription();
        }
    }

    @Getter
    public static class ConversationDto {
        private Long placeId; // 라우팅을 위해 필요
        private String placeName; // 장소 이름도 보여주면 좋음
        private String englishText1;
        private String koreanText1;

        public ConversationDto(Conversation c) {
            this.placeId = c.getPlace().getId();
            this.placeName = c.getPlace().getName();
            this.englishText1 = c.getEnglishText1();
            this.koreanText1 = c.getKoreanText1();
        }
    }
}