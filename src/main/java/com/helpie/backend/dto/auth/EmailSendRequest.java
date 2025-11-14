package com.helpie.backend.dto.auth;

import com.helpie.backend.domain.email.AuthType;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "이메일 인증 요청 DTO")
public record EmailSendRequest(
        @Schema(
                description = "인증 메일을 받을 이메일 주소",
                example = "user@example.com",
                required = true
        )
        String email,

        @Schema(
                description = """
                        어떤 유형의 이메일 인증을 보낼지 구분하는 타입  
                        - EMAIL_AUTH : 회원가입 / 로그인 이메일 인증  
                        - PW_AUTH : 비밀번호 재설정 인증  
                        """,
                example = "PW_AUTH",
                required = true,
                implementation = AuthType.class
        )
        AuthType authType
) {
}
