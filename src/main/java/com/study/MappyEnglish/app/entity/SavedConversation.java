package com.study.MappyEnglish.app.entity;

import com.study.MappyEnglish.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved_conversations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "conversation_id"})
})
@Getter @Setter
@NoArgsConstructor
public class SavedConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 기존 User 엔티티

    @ManyToOne(fetch = FetchType.EAGER) // 대화 내용을 바로 보여줘야 하므로 EAGER 권장 (상황에 따라 조절)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation; // 기존 Conversation 엔티티

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // 생성 편의 메서드
    public static SavedConversation create(User user, Conversation conversation) {
        SavedConversation sc = new SavedConversation();
        sc.setUser(user);
        sc.setConversation(conversation);
        return sc;
    }
}