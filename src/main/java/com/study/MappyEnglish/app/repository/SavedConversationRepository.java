package com.study.MappyEnglish.app.repository;

import com.study.MappyEnglish.app.entity.SavedConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface SavedConversationRepository extends JpaRepository<SavedConversation, Long> {

    // 특정 유저의 저장 목록 조회 (최신순)
    List<SavedConversation> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    // 이미 저장된 대화인지 확인
    boolean existsByUserIdAndConversationId(Long userId, Long conversationId);

    // 저장 취소(삭제)를 위해 찾기
    Optional<SavedConversation> findByUserIdAndConversationId(Long userId, Long conversationId);
}
