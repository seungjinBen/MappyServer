package com.study.MappyEnglish.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "places")   // 테이블명 정확히
public class Place {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;

    // 길이가 긴 설명을 고려해 TEXT로 (PostgreSQL)
    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "city_id")
    private Long cityId;

    // URL은 길이가 길 수 있어 보통 2048 정도로
    @Column(name = "img_url", length = 2048)
    private String imgUrl;

    // 기본 생성자 + getter/setter
    public Place() {}
    // getters/setters...
    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }
    public String getCategory(){ return category; }
    public void setCategory(String category){ this.category = category; }
    public Double getLat(){ return lat; }
    public void setLat(Double lat){ this.lat = lat; }
    public Double getLng(){ return lng; }
    public void setLng(Double lng){ this.lng = lng; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getcityId(){ return cityId; }
    public void setcityId(Long cityId){ this.cityId = cityId; }
    public String getImgUrl() { return imgUrl; }
    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }
}