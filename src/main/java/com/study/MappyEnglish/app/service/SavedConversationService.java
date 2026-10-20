package com.study.MappyEnglish.app.service;

import com.study.MappyEnglish.app.dto.SavedConversationResponseDto;
import com.study.MappyEnglish.app.entity.Conversation;
import com.study.MappyEnglish.app.entity.SavedConversation;
import com.study.MappyEnglish.user.User;
import com.study.MappyEnglish.app.repository.ConversationRepository;
import com.study.MappyEnglish.app.repository.SavedConversationRepository;
import com.study.MappyEnglish.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SavedConversationService {

    private final SavedConversationRepository savedRepository;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;

    // 1. 저장하기 (인자 변경: Long userId -> String email)
    public void saveConversation(String email, Long conversationId) {
        // 이메일로 유저 정보 먼저 가져오기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 이메일의 유저를 찾을 수 없습니다: " + email));

        // 이미 저장했는지 확인 (user.getId() 활용)
        if (savedRepository.existsByUserIdAndConversationId(user.getId(), conversationId)) {
            throw new RuntimeException("이미 저장된 대화입니다.");
        }

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("대화를 찾을 수 없습니다."));

        SavedConversation saved = SavedConversation.create(user, conversation);
        savedRepository.save(saved);
    }

    // 2. 조회하기 (인자 변경: Long userId -> String email)
    @Transactional(readOnly = true)
    public List<SavedConversationResponseDto> getMySavedConversations(String email) {
        // 이메일로 유저 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저 찾기 실패"));

        return savedRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(SavedConversationResponseDto::new)
                .collect(Collectors.toList());
    }

    // 3. 삭제하기 (인자 변경: Long userId -> String email)
    public void deleteSavedConversation(String email, Long conversationId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저 찾기 실패"));

        SavedConversation saved = savedRepository.findByUserIdAndConversationId(user.getId(), conversationId)
                .orElseThrow(() -> new RuntimeException("저장된 내역이 없습니다."));

        savedRepository.delete(saved);
    }
}
