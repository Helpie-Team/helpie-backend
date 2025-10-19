package com.helpie.backend.dto.auth;

import lombok.Builder;

@Builder
public record ResponseCookie(
    String refreshToken,
    int maxAge,
    boolean httpOnly,
    String path,
    String sameSite,
    boolean secure
) {
}
