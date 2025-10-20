package com.helpie.backend.domain.sociallogin;

public enum SocialType {
    GOOGLE("GOOGLE"),
    KAKAO("KAKAO");

    private String type;

    SocialType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
