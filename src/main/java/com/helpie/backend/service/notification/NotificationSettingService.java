package com.helpie.backend.service.notification;

import com.helpie.backend.dto.notification.NotificationSettingResponse;
import com.helpie.backend.dto.notification.NotificationSettingUpdateRequest;

/**
 * 알림 설정 서비스 인터페이스
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
public interface NotificationSettingService {
    
    /**
     * 사용자의 알림 설정 조회
     */
    NotificationSettingResponse getNotificationSettings(Long userId);
    
    /**
     * 사용자의 알림 설정 변경
     */
    NotificationSettingResponse updateNotificationSettings(Long userId, NotificationSettingUpdateRequest request);
    
    /**
     * 댓글 알림 수신 가능 여부 확인
     */
    boolean canReceiveCommentNotification(Long userId);
    
    /**
     * 좋아요 알림 수신 가능 여부 확인
     */
    boolean canReceiveLikeNotification(Long userId);
}