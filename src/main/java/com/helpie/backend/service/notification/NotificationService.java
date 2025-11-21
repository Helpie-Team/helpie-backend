package com.helpie.backend.service.notification;

import com.helpie.backend.dto.notification.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 알림 서비스 인터페이스
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
public interface NotificationService {
    
    /**
     * 댓글 알림 발송
     */
    void sendCommentNotification(Long receiverId, Long communityId, String communityTitle, Long actorId, String actorName);
    
    /**
     * 좋아요 알림 발송
     */
    void sendLikeNotification(Long receiverId, Long communityId, String communityTitle, Long actorId, String actorName);
    
    /**
     * 사용자 알림 목록 조회
     */
    Page<NotificationResponse> getNotifications(Long userId, Pageable pageable);
    
    /**
     * 읽지 않은 알림 개수 조회
     */
    Long getUnreadCount(Long userId);
    
    /**
     * 알림 읽음 처리
     */
    void markAsRead(Long notificationId, Long userId);
    
    /**
     * 모든 알림 읽음 처리
     */
    void markAllAsRead(Long userId);
    
    /**
     * 알림 삭제
     */
    void deleteNotification(Long notificationId, Long userId);
    
    /**
     * 모든 알림 삭제
     */
    void deleteAllNotifications(Long userId);
}