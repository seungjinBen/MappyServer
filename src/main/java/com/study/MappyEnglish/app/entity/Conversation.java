package com.study.MappyEnglish.app.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK: conversations.place_id → places.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(name = "type", columnDefinition = "char(1)")
    private Character type;

    @Column(name = "english_text1", columnDefinition = "text")
    private String englishText1;
    @Column(name = "korean_text1", columnDefinition = "text")
    private String koreanText1;
    @Column(name = "audio_url1")
    private String audioUrl1;

    @Column(name = "english_text2", columnDefinition = "text")
    private String englishText2;
    @Column(name = "korean_text2", columnDefinition = "text")
    private String koreanText2;
    @Column(name = "audio_url2")
    private String audioUrl2;

    @Column(name = "english_text3", columnDefinition = "text")
    private String englishText3;
    @Column(name = "korean_text3", columnDefinition = "text")
    private String koreanText3;
    @Column(name = "audio_url3")
    private String audioUrl3;

    @Column(name = "english_text4", columnDefinition = "text")
    private String englishText4;
    @Column(name = "korean_text4", columnDefinition = "text")
    private String koreanText4;
    @Column(name = "audio_url4")
    private String audioUrl4;

    @Column(name = "english_text5", columnDefinition = "text")
    private String englishText5;
    @Column(name = "korean_text5", columnDefinition = "text")
    private String koreanText5;
    @Column(name = "audio_url5")
    private String audioUrl5;
    @Column(name = "category")
    private String category;
    @Column(name = "section")
    private String section;
}