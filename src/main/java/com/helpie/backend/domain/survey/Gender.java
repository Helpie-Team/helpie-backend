package com.helpie.backend.domain.survey;

/**
 * 성별 구분 Enum
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
public enum Gender {
    MALE("남자"),
    FEMALE("여자"),
    OTHER("기타");

    private final String description;

    Gender(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}