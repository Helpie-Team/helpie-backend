package com.helpie.backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignUpRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String username,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {
}
