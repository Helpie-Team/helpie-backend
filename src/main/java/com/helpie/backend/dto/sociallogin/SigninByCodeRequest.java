package com.helpie.backend.dto.sociallogin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SigninByCodeRequest(
        @NotNull
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "소셜 로그인 OAuth 서버에서 전달받은 code값")
        String code,
        @NotNull
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "소셜 로그인에 사용했던 OAuth redirectUri")
        String redirectUri
) {
}
