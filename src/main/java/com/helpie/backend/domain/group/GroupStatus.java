package com.helpie.backend.domain.group;

/**
 * 소모임 상태 Enum
 RECRUTING: 현재 시간이 Group 의 endAt(마감기한) 이전
 RECRUITMENT_CLOSED: currentMember와 maxMember이 같은 경우
 COMPLETED: 현재 시간이 Group의 endAt 이후
 */
public enum GroupStatus {
    RECRUITING("모집중"),
    RECRUITMENT_CLOSED("모집마감"),
    COMPLETED("모임완료");

    private final String description;

    GroupStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}