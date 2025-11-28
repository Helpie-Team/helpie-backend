package com.helpie.backend.dto.notification;

import com.helpie.backend.domain.notification.NotificationSetting;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 알림 설정 조회 응답 DTO
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Builder
@Schema(description = "알림 설정 응답")
public record NotificationSettingResponse(
    
    @Schema(description = "전체 알림 설정", example = "true")
    boolean allNotifications,
    
    @Schema(description = "댓글 알림 설정", example = "true")
    boolean commentNotifications,
    
    @Schema(description = "좋아요 알림 설정", example = "true")
    boolean likeNotifications
) {
    
    /**
     * NotificationSetting 엔티티로부터 응답 DTO 생성
     */
    public static NotificationSettingResponse from(NotificationSetting setting) {
        return NotificationSettingResponse.builder()
            .allNotifications(setting.isAllNotifications())
            .commentNotifications(setting.isCommentNotifications())
            .likeNotifications(setting.isLikeNotifications())
            .build();
    }
    
    /**
     * 기본 설정으로 응답 DTO 생성
     */
    public static NotificationSettingResponse createDefault() {
        return NotificationSettingResponse.builder()
            .allNotifications(true)
            .commentNotifications(true)
            .likeNotifications(true)
            .build();
    }
}