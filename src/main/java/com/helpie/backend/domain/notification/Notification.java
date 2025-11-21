package com.helpie.backend.domain.notification;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 알림 엔티티
 * 사용자에게 발송된 알림 정보를 저장합니다.
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 알림을 받을 사용자 ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 알림 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    /**
     * 알림 제목
     */
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    /**
     * 알림 메시지
     */
    @Column(name = "message", nullable = false, length = 500)
    private String message;

    /**
     * 관련 리소스 ID (게시글 ID, 댓글 ID 등)
     */
    @Column(name = "related_id")
    private Long relatedId;

    /**
     * 관련 리소스 타입 (community, comment 등)
     */
    @Column(name = "related_type", length = 50)
    private String relatedType;

    /**
     * 알림 발생시킨 사용자 ID (댓글/좋아요 작성자)
     */
    @Column(name = "actor_id")
    private Long actorId;

    /**
     * 알림 발생시킨 사용자명
     */
    @Column(name = "actor_name", length = 50)
    private String actorName;

    /**
     * 읽음 여부
     */
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean isRead = false;

    /**
     * 알림 생성 일시
     */
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 알림 읽은 일시
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Builder
    public Notification(Long userId, NotificationType type, String title, String message, 
                       Long relatedId, String relatedType, Long actorId, String actorName) {
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.relatedId = relatedId;
        this.relatedType = relatedType;
        this.actorId = actorId;
        this.actorName = actorName;
        this.isRead = false;
    }

    /**
     * 알림 읽음 처리
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * 댓글 알림 생성
     */
    public static Notification createCommentNotification(Long userId, Long communityId, 
                                                        Long actorId, String actorName, 
                                                        String communityTitle) {
        return Notification.builder()
            .userId(userId)
            .type(NotificationType.COMMENT)
            .title("새 댓글 알림")
            .message(String.format("%s님이 '%s' 게시글에 댓글을 달았습니다.", actorName, 
                    communityTitle.length() > 20 ? communityTitle.substring(0, 20) + "..." : communityTitle))
            .relatedId(communityId)
            .relatedType("community")
            .actorId(actorId)
            .actorName(actorName)
            .build();
    }

    /**
     * 좋아요 알림 생성
     */
    public static Notification createLikeNotification(Long userId, Long communityId, 
                                                     Long actorId, String actorName, 
                                                     String communityTitle) {
        return Notification.builder()
            .userId(userId)
            .type(NotificationType.LIKE)
            .title("새 좋아요 알림")
            .message(String.format("%s님이 '%s' 게시글에 좋아요를 눌렀습니다.", actorName, 
                    communityTitle.length() > 20 ? communityTitle.substring(0, 20) + "..." : communityTitle))
            .relatedId(communityId)
            .relatedType("community")
            .actorId(actorId)
            .actorName(actorName)
            .build();
    }

}