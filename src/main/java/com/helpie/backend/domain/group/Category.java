package com.helpie.backend.domain.group;

public enum Category {
    ALL("전체"),
    HOBBY("문화, 취미"),
    ART("예술, 클래스"),
    LIFE("액티비티, 라이프"),
    STUDY("자기계발, 공부"),
    SOCIAL("사회, 친목");

    private final String description;

    Category(String description) {
        this.description = description;
    }


}
