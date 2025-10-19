package com.helpie.backend.domain.survey;

/**
 * 사용 언어 Enum
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
public enum Language {
    KOREAN("한국어"),
    ENGLISH("English"),
    CHINESE("中國語"),
    JAPANESE("日本語"),
    SPANISH("español"),
    FRENCH("français");

    private final String description;

    Language(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}