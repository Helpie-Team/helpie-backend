package com.helpie.backend.domain.chatroom;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 채팅 메시지 엔티티
 * 채팅방 내 메시지 (일반 메시지 + 시스템 메시지)
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Entity
@Table(name = "chat_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(name = "sender_id")
    private Long senderId; // 시스템 메시지의 경우 null

    @Column(name = "sender_name", length = 50)
    private String senderName; // 시스템 메시지 표시용

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    // 일반 메시지 생성자
    public ChatMessage(ChatRoom chatRoom, Long senderId, String senderName, String content) {
        this.chatRoom = chatRoom;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.messageType = MessageType.TEXT;
        this.sentAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    // 시스템 메시지 생성자
    public ChatMessage(ChatRoom chatRoom, String content, MessageType messageType) {
        this.chatRoom = chatRoom;
        this.senderId = null;
        this.senderName = "시스템";
        this.content = content;
        this.messageType = messageType;
        this.sentAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    /**
     * 메시지를 삭제합니다.
     */
    public void delete() {
        this.isDeleted = true;
    }

    /**
     * 시스템 메시지인지 확인합니다.
     */
    public boolean isSystemMessage() {
        return this.messageType != MessageType.TEXT;
    }
}