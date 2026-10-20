package com.study.MappyEnglish.app.service;

import com.study.MappyEnglish.app.dto.MissionCompleteRequestDto;
import com.study.MappyEnglish.app.dto.MissionProgressResponseDto;
import com.study.MappyEnglish.app.entity.UserMissionLog;
import com.study.MappyEnglish.user.User;
import com.study.MappyEnglish.user.UserRepository;
import com.study.MappyEnglish.app.repository.UserMissionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final UserMissionLogRepository missionLogRepository;
    private final UserRepository userRepository;

    // 이메일로 유저 ID를 찾아주는 공통 헬퍼 메서드
    private Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."))
                .getId(); // User 객체를 다 넘길 필요 없이, ID만
    }

    @Transactional
    public void completeMission(String email, MissionCompleteRequestDto dto) {
        Long userId = getUserIdByEmail(email);

        // 이미 깬 미션이면 무시 (중복 저장 방지)
        if (!missionLogRepository.existsByUserIdAndConversationId(userId, dto.getConversationId())) {
            UserMissionLog log = new UserMissionLog(userId, dto.getPlaceId(), dto.getConversationId());
            missionLogRepository.save(log);
        }
    }

    @Transactional(readOnly = true)
    public List<MissionProgressResponseDto> getMyProgress(String email) {
        return missionLogRepository.findProgressByUserId(getUserIdByEmail(email));
    }

    @Transactional(readOnly = true)
    public List<MissionCompleteRequestDto> getCompletedDetails(String email) {
        return missionLogRepository.findAllCompletedDetailsByUserId(getUserIdByEmail(email));
    }
}