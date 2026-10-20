package com.study.MappyEnglish.app.repository;

import com.study.MappyEnglish.app.entity.UserMissionLog;
import com.study.MappyEnglish.app.dto.MissionProgressResponseDto;
import com.study.MappyEnglish.app.dto.MissionCompleteRequestDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserMissionLogRepository extends JpaRepository<UserMissionLog, Long> {

    // 이미 완료한 미션인지 중복 체크
    boolean existsByUserIdAndConversationId(Long userId, Long conversationId);

    // 2바텀 시트용: 유저의 장소별 미션 완료 개수를 묶어서(GROUP BY) 반환
    @Query("SELECT new com.study.MappyEnglish.app.dto.MissionProgressResponseDto(u.placeId, COUNT(u)) " +
            "FROM UserMissionLog u WHERE u.userId = :userId GROUP BY u.placeId")
    List<MissionProgressResponseDto> findProgressByUserId(@Param("userId") Long userId);
    // 유저가 깬 모든 미션의 상세 내역(장소ID, 대화ID)을 가져옴
    @Query("SELECT new com.study.MappyEnglish.app.dto.MissionCompleteRequestDto(u.placeId, u.conversationId) " +
            "FROM UserMissionLog u WHERE u.userId = :userId")
    List<MissionCompleteRequestDto> findAllCompletedDetailsByUserId(@Param("userId") Long userId);
}