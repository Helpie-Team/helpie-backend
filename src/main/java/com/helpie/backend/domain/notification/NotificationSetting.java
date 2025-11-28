package com.helpie.backend.domain.notification;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 알림 설정 엔티티
 * 사용자별 알림 수신 설정을 관리합니다.
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notification_setting")
public class NotificationSetting {

    @Id
    @Column(name = "user_id")
    private Long userId;

    /**
     * 전체 알림 설정
     * false인 경우 모든 알림이 비활성화됩니다.
     */
    @Column(name = "all_notifications", nullable = false)
    private boolean allNotifications = true;


    /**
     * 댓글 알림 설정
     * 내가 작성한 커뮤니티 게시글에 댓글이 달릴 때 알림 수신 여부
     */
    @Column(name = "comment_notifications", nullable = false)
    private boolean commentNotifications = true;

    /**
     * 좋아요 알림 설정
     * 내가 작성한 커뮤니티 게시글에 좋아요가 눌릴 때 알림 수신 여부
     */
    @Column(name = "like_notifications", nullable = false)
    private boolean likeNotifications = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public NotificationSetting(Long userId, boolean allNotifications, 
                              boolean commentNotifications, boolean likeNotifications) {
        this.userId = userId;
        this.allNotifications = allNotifications;
        this.commentNotifications = commentNotifications;
        this.likeNotifications = likeNotifications;
    }

    /**
     * 기본 알림 설정으로 생성
     */
    public static NotificationSetting createDefault(Long userId) {
        return NotificationSetting.builder()
            .userId(userId)
            .allNotifications(true)
            .commentNotifications(true)
            .likeNotifications(true)
            .build();
    }

    /**
     * 전체 알림 설정 변경
     */
    public void updateAllNotifications(boolean allNotifications) {
        this.allNotifications = allNotifications;
    }

    /**
     * 개별 알림 설정 변경
     */
    public void updateNotificationSettings(boolean commentNotifications, 
                                         boolean likeNotifications) {
        this.commentNotifications = commentNotifications;
        this.likeNotifications = likeNotifications;
    }

    /**
     * 댓글 알림 수신 가능 여부 확인
     */
    public boolean canReceiveCommentNotification() {
        return allNotifications && commentNotifications;
    }

    /**
     * 좋아요 알림 수신 가능 여부 확인
     */
    public boolean canReceiveLikeNotification() {
        return allNotifications && likeNotifications;
    }

}