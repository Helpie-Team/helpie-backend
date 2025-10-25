package com.helpie.backend.domain.group;

/**
 * 소모임 상태 Enum
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
public enum GroupStatus {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    FULL("만원");

    private final String description;

    GroupStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}