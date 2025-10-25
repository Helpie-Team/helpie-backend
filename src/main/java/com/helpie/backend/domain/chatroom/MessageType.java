package com.helpie.backend.domain.chatroom;

/**
 * 메시지 타입 Enum
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
public enum MessageType {
    TEXT("텍스트"),
    SYSTEM_JOIN("시스템-입장"),
    SYSTEM_LEAVE("시스템-퇴장"),
    SYSTEM_NOTICE("시스템-공지");

    private final String description;

    MessageType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}