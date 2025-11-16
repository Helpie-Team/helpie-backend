package com.helpie.backend.dto.auth;

import com.helpie.backend.domain.user.UserRegexp;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record SignUpRequest(
        @NotNull
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @NotNull
        @Schema(
                requiredMode = Schema.RequiredMode.REQUIRED,
                description = "유저 별명",
                minLength = 2,
                maxLength = 12,
                pattern = UserRegexp.USERNAME_REGEXP
        )
        @Pattern(regexp = UserRegexp.USERNAME_REGEXP)
        @Length(min = 2, max = 12)
        String username,
        @NotNull
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {
}
