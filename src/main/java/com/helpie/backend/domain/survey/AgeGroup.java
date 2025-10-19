package com.helpie.backend.domain.survey;

/**
 * 연령대 구분 Enum
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
public enum AgeGroup {
    TEENS("10대"),
    TWENTIES("20대"),
    THIRTIES("30대"),
    FORTIES("40대"),
    OTHER("기타");

    private final String description;

    AgeGroup(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}