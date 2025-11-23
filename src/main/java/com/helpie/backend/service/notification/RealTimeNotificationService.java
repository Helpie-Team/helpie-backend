package com.helpie.backend.service.notification;

import com.helpie.backend.dto.notification.NotificationResponse;
import com.helpie.backend.dto.notification.RealTimeNotificationMessage;
import com.helpie.backend.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * 실시간 알림 서비스
 * WebSocket을 통한 실시간 알림 전송 담당
 * 
 * @author 전우선
 * @since 2025-11-24(월)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RealTimeNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;

    /**
     * 특정 사용자에게 실시간 알림 전송
     */
    public void sendNotificationToUser(Long userId, NotificationResponse notification) {
        try {
            // 현재 사용자의 전체 읽지 않은 알림 개수 조회
            Long unreadCount = notificationRepository.countUnreadByUserId(userId);
            
            // 실시간 알림 메시지 생성
            RealTimeNotificationMessage message = RealTimeNotificationMessage.from(notification, unreadCount);
            
            // WebSocket을 통해 특정 사용자에게 알림 전송
            // 구독 주소: /topic/notifications/{userId}
            String destination = "/topic/notifications/" + userId;
            messagingTemplate.convertAndSend(destination, message);
            
            log.info("실시간 알림 전송 완료 - userId: {}, notificationId: {}, destination: {}", 
                    userId, notification.id(), destination);
                    
        } catch (Exception e) {
            log.error("실시간 알림 전송 실패 - userId: {}, notificationId: {}, error: {}", 
                     userId, notification.id(), e.getMessage(), e);
        }
    }

    /**
     * 사용자의 읽지 않은 알림 개수 업데이트 전송
     * 알림 읽음 처리나 삭제 시 호출
     */
    public void sendUnreadCountUpdate(Long userId) {
        try {
            Long unreadCount = notificationRepository.countUnreadByUserId(userId);
            
            // 읽지 않은 알림 개수만 전송
            String destination = "/topic/notifications/" + userId + "/count";
            messagingTemplate.convertAndSend(destination, unreadCount);
            
            log.info("읽지 않은 알림 개수 업데이트 전송 - userId: {}, count: {}", userId, unreadCount);
            
        } catch (Exception e) {
            log.error("읽지 않은 알림 개수 업데이트 실패 - userId: {}, error: {}", userId, e.getMessage(), e);
        }
    }
}