package com.helpie.backend.event.notification;

import com.helpie.backend.dto.notification.NotificationResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 실시간 알림 이벤트
 * 알림 생성 시 WebSocket을 통한 실시간 전송을 위한 이벤트
 * 
 * @author 전우선
 * @since 2025-11-24(월)
 */
@Getter
@RequiredArgsConstructor
public class NotificationEvent {
    
    private final Long userId;
    private final NotificationResponse notification;
}