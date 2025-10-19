package com.helpie.backend.domain.survey;

/**
 * 국가 선택 Enum
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
public enum Country {
    KOREA("한국"),
    USA("미국"),
    CHINA("중국"),
    JAPAN("일본"),
    UK("영국"),
    OTHER("그 외 나라");

    private final String description;

    Country(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}