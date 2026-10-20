package com.study.MappyEnglish.app.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConversationDto implements Serializable {

    private Long id;
    private Character type;
    private String category;
    private String section;
    private PlaceBriefDto place;      // ← Place 요약 정보
    private List<LineDto> lines = new ArrayList<>();

    public ConversationDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Character getType() { return type; }
    public void setType(Character type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public PlaceBriefDto getPlace() { return place; }
    public void setPlace(PlaceBriefDto place) { this.place = place; }

    public List<LineDto> getLines() { return lines; }
    public void setLines(List<LineDto> lines) {
        this.lines = (lines != null) ? lines : new ArrayList<>();
    }
    public void addLine(LineDto line) { if (line != null) this.lines.add(line); }

    // ----- Nested DTOs -----

    public static class PlaceBriefDto implements Serializable {
        private Long id;
        private String name;
        private String category;
        private Double lat;
        private Double lng;

        public PlaceBriefDto() {}

        public PlaceBriefDto(Long id, String name, String category, Double lat, Double lng) {
            this.id = id; this.name = name; this.category = category; this.lat = lat; this.lng = lng;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Double getLat() { return lat; }
        public void setLat(Double lat) { this.lat = lat; }
        public Double getLng() { return lng; }
        public void setLng(Double lng) { this.lng = lng; }
    }

    public static class LineDto implements Serializable {
        private Integer lineOrder;
        private String englishText;
        private String koreanText;
        private String audioUrl;

        public LineDto() {}
        public LineDto(Integer lineOrder, String englishText, String koreanText, String audioUrl) {
            this.lineOrder = lineOrder;
            this.englishText = englishText;
            this.koreanText = koreanText;
            this.audioUrl = audioUrl;
        }

        public Integer getLineOrder() { return lineOrder; }
        public void setLineOrder(Integer lineOrder) { this.lineOrder = lineOrder; }
        public String getEnglishText() { return englishText; }
        public void setEnglishText(String englishText) { this.englishText = englishText; }
        public String getKoreanText() { return koreanText; }
        public void setKoreanText(String koreanText) { this.koreanText = koreanText; }
        public String getAudioUrl() { return audioUrl; }
        public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    }
}