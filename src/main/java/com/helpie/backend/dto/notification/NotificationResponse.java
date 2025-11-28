package com.helpie.backend.dto.notification;

import com.helpie.backend.domain.notification.Notification;
import com.helpie.backend.domain.notification.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 알림 응답 DTO
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Builder
@Schema(description = "알림 응답")
public record NotificationResponse(
    
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
    
    @Schema(description = "알림 발생시킨 사용자 ID", example = "456")
    Long actorId,
    
    @Schema(description = "알림 발생시킨 사용자명", example = "홍길동")
    String actorName,
    
    @Schema(description = "읽음 여부", example = "false")
    boolean isRead,
    
    @Schema(description = "알림 생성일시", example = "2025-11-21T10:30:00")
    LocalDateTime createdAt,
    
    @Schema(description = "알림 읽은 일시", example = "2025-11-21T11:00:00")
    LocalDateTime readAt
) {
    
    /**
     * Notification 엔티티로부터 응답 DTO 생성
     */
    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
            .id(notification.getId())
            .type(notification.getType())
            .title(notification.getTitle())
            .message(notification.getMessage())
            .relatedId(notification.getRelatedId())
            .relatedType(notification.getRelatedType())
            .actorId(notification.getActorId())
            .actorName(notification.getActorName())
            .isRead(notification.isRead())
            .createdAt(notification.getCreatedAt())
            .readAt(notification.getReadAt())
            .build();
    }
}