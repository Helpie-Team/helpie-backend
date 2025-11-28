package com.helpie.backend.event.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 알림 개수 업데이트 이벤트
 * 알림 읽음/삭제 시 읽지 않은 알림 개수 업데이트를 위한 이벤트
 * 
 * @author 전우선
 * @since 2025-11-24(월)
 */
@Getter
@RequiredArgsConstructor
public class NotificationCountUpdateEvent {
    
    private final Long userId;
}