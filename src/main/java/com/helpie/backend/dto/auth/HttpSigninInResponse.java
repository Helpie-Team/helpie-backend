package com.helpie.backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record HttpSigninInResponse(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    String accessToken,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    String refreshToken
) {
}
