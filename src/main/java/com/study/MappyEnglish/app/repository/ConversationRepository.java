package com.study.MappyEnglish.app.repository;


import com.study.MappyEnglish.app.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    // 단건 조회 시 Place까지 한 번에
    @Query("""
           select c from Conversation c
           join fetch c.place
           where c.id = :id
           """)
    Optional<Conversation> findByIdWithPlace(@Param("id") Long id);

    // 특정 placeId의 대화 목록 (Place를 함께 가져와도 되고, 안 가져와도 됨)
    @Query("""
           select c from Conversation c
           where c.place.id = :placeId
           """)
    List<Conversation> findAllByPlaceId(@Param("placeId") Long placeId);

    // placeId로 찾되, id 오름차순으로 정렬해서 가져오기
    List<Conversation> findAllByPlaceIdOrderByIdAsc(Long placeId);
    // 목록에서도 Place까지 필요하면 아래처럼 fetch join 버전 사용
    @Query("""
           select c from Conversation c
           join fetch c.place
           where c.place.id = :placeId
           """)
    List<Conversation> findAllByPlaceIdWithPlace(@Param("placeId") Long placeId);
    // 한국어 해석에서 키워드 검색 (장소 정보도 같이 가져오기 위해 EntityGraph 사용 권장하지만, 일단 기본 쿼리)
    List<Conversation> findByKoreanText1ContainingIgnoreCase(String keyword);
    List<Conversation> findByCategory(String category);
    List<Conversation> findBySection(String section);
}
