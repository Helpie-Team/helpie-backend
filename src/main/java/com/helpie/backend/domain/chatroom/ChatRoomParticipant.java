package com.helpie.backend.domain.chatroom;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 채팅방 참여자 엔티티
 * 현재 채팅방에 입장해 있는 사용자를 관리합니다.
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Entity
@Table(name = "chatroom_participants", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"chatroom_id", "user_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "entered_at", nullable = false)
    private LocalDateTime enteredAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "is_online", nullable = false)
    private Boolean isOnline;

    public ChatRoomParticipant(ChatRoom chatRoom, Long userId) {
        this.chatRoom = chatRoom;
        this.userId = userId;
        this.enteredAt = LocalDateTime.now();
        this.isOnline = true;
    }

    /**
     * 채팅방에서 나갑니다.
     */
    public void leave() {
        this.isOnline = false;
        this.leftAt = LocalDateTime.now();
    }

    /**
     * 채팅방에 다시 입장합니다.
     */
    public void enter() {
        this.isOnline = true;
        this.leftAt = null;
        this.enteredAt = LocalDateTime.now();
    }
}