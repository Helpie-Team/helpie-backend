package com.helpie.backend.dto.notification;

import com.helpie.backend.domain.notification.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 실시간 알림 메시지 DTO
 * WebSocket을 통해 전송되는 알림 데이터
 * 
 * @author 전우선
 * @since 2025-11-24(월)
 */
@Builder
@Schema(description = "실시간 알림 메시지")
public record RealTimeNotificationMessage(
    
    @Schema(description = "알림 ID", example = "1")
    Long id,
    
    @Schema(description = "알림 타입", example = "COMMENT")
    NotificationType type,
    
    @Schema(description = "알림 제목", example = "새 댓글 알림")
    String title,
    
    @Schema(description = "알림 메시지", example = "홍길동님이 '도움이 되는 정보...' 게시글에 댓글을 달았습니다.")
    String message,
    
    @Schema(description = "관련 리소스 ID", example = "123")
    Long relatedId,
    
    @Schema(description = "관련 리소스 타입", example = "community")
    String relatedType,
    
    @Schema(description = "알림 발생시킨 사용자명", example = "홍길동")
    String actorName,
    
    @Schema(description = "알림 생성일시", example = "2025-11-21T10:30:00")
    LocalDateTime createdAt,
    
    @Schema(description = "전체 읽지 않은 알림 개수", example = "5")
    Long unreadCount
) {
    
    /**
     * NotificationResponse에서 실시간 메시지 생성
     */
    public static RealTimeNotificationMessage from(NotificationResponse notification, Long unreadCount) {
        return RealTimeNotificationMessage.builder()
            .id(notification.id())
            .type(notification.type())
            .title(notification.title())
            .message(notification.message())
            .relatedId(notification.relatedId())
            .relatedType(notification.relatedType())
            .actorName(notification.actorName())
            .createdAt(notification.createdAt())
            .unreadCount(unreadCount)
            .build();
    }
}