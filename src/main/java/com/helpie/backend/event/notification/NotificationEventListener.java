package com.helpie.backend.event.notification;

import com.helpie.backend.service.notification.RealTimeNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 알림 이벤트 리스너
 * 알림 관련 이벤트를 처리하여 실시간 WebSocket 전송을 담당
 * 
 * @author 전우선
 * @since 2025-11-24(월)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final RealTimeNotificationService realTimeNotificationService;

    /**
     * 새로운 알림 생성 이벤트 처리
     * 비동기로 실시간 알림을 전송
     */
    @Async("websocketTaskExecutor")
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        log.debug("알림 이벤트 처리 시작 - userId: {}, notificationId: {}", 
                 event.getUserId(), event.getNotification().id());
        
        try {
            realTimeNotificationService.sendNotificationToUser(
                event.getUserId(), 
                event.getNotification()
            );
        } catch (Exception e) {
            log.error("알림 이벤트 처리 실패 - userId: {}, notificationId: {}, error: {}", 
                     event.getUserId(), event.getNotification().id(), e.getMessage(), e);
        }
    }

    /**
     * 알림 개수 업데이트 이벤트 처리
     * 비동기로 읽지 않은 알림 개수를 업데이트
     */
    @Async("websocketTaskExecutor")
    @EventListener
    public void handleNotificationCountUpdateEvent(NotificationCountUpdateEvent event) {
        log.debug("알림 개수 업데이트 이벤트 처리 시작 - userId: {}", event.getUserId());
        
        try {
            realTimeNotificationService.sendUnreadCountUpdate(event.getUserId());
        } catch (Exception e) {
            log.error("알림 개수 업데이트 이벤트 처리 실패 - userId: {}, error: {}", 
                     event.getUserId(), e.getMessage(), e);
        }
    }
}