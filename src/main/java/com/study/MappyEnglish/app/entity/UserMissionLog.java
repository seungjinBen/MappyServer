package com.study.MappyEnglish.app.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "user_mission_log",
        // 한 유저가 똑같은 대화를 여러 번 성공해도 DB에 중복 저장되지 않도록 막기
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "conversation_id"})
        }
)
public class UserMissionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 실제로는 연관관계 매핑(@ManyToOne)을 할 수 있지만,
    // 성능과 단순함을 위해 유저의 숫자 ID(Long)만 저장
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    // 미션을 언제 완료했는지 기록하는 시간 (업데이트할 일은 없으므로 updatable = false)
    @Column(name = "completed_at", nullable = false, updatable = false)
    private LocalDateTime completedAt;

    public UserMissionLog(Long userId, Long placeId, Long conversationId) {
        this.userId = userId;
        this.placeId = placeId;
        this.conversationId = conversationId;
        this.completedAt = LocalDateTime.now(); // 객체가 생성되는 순간의 시간을 자동으로 쾅! 찍어줍니다.
    }
}