package com.study.MappyEnglish.app.repository;

import com.study.MappyEnglish.app.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    // "SELECT * FROM places WHERE name LIKE %query% OR description LIKE %query%" 와 같은 뜻입니다.
    // Containing: 앞뒤로 뭐가 붙든 포함되면 찾음
    // IgnoreCase: 대소문자 구분 안 함 (Paris = paris)
    // 설명(description) 검색 제거하고 이름만 검색
    List<Place> findByNameContainingIgnoreCase(String name);
}
