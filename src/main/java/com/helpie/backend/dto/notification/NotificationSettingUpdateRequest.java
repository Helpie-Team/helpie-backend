package com.helpie.backend.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 알림 설정 변경 요청 DTO
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Schema(description = "알림 설정 변경 요청")
public record NotificationSettingUpdateRequest(
    
    @NotNull(message = "전체 알림 설정은 필수입니다")
    @Schema(description = "전체 알림 설정", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    Boolean allNotifications,
    
    @NotNull(message = "댓글 알림 설정은 필수입니다")
    @Schema(description = "댓글 알림 설정", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    Boolean commentNotifications,
    
    @NotNull(message = "좋아요 알림 설정은 필수입니다")
    @Schema(description = "좋아요 알림 설정", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    Boolean likeNotifications
) {
}