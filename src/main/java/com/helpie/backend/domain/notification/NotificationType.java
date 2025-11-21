package com.helpie.backend.domain.notification;

/**
 * 알림 타입 열거형
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
public enum NotificationType {
    /**
     * 댓글 알림
     */
    COMMENT("댓글"),
    
    /**
     * 좋아요 알림  
     */
    LIKE("좋아요");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}