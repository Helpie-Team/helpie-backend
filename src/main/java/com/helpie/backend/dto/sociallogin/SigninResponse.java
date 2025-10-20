package com.helpie.backend.dto.sociallogin;

public record SigninResponse(
        String accessToken,
        String refreshToken
) {
}
