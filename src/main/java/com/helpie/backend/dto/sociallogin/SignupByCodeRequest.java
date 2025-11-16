package com.helpie.backend.dto.sociallogin;

import com.helpie.backend.domain.user.UserRegexp;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record SignupByCodeRequest(
        @NotNull
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "소셜 로그인 OAuth 서버에서 전달받은 accessToken값")
        String socialAccessToken,
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
        @Schema(
                requiredMode = Schema.RequiredMode.REQUIRED,
                description = "유저 이메일"
        )
                String email
) {
}
