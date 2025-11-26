package com.helpie.backend.domain.chatroom;

import com.helpie.backend.domain.group.Group;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 채팅방 엔티티
 * 소모임 내부의 채팅방 (소모임 멤버만 입장 가능)
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Entity
@Table(name = "chat_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ChatRoomParticipant> participants = new HashSet<>();

    @Column(name = "current_participants", nullable = false)
    private Integer currentParticipants;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ChatRoom(Group group) {
        this.group = group;
        this.title = group.getTitle() + " 채팅방";
        this.currentParticipants = group.getCurrentMembers(); // 소모임 가입 멤버 수로 초기화
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 채팅방에 참여자를 추가합니다.
     */
    public void addParticipant(Long userId) {
        this.currentParticipants++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 채팅방에서 참여자를 제거합니다.
     */
    public void removeParticipant(Long userId) {
        this.currentParticipants--;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 채팅방을 비활성화합니다.
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 채팅방을 활성화합니다.
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }
}