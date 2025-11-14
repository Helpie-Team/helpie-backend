package com.helpie.backend.dto.auth;

import com.helpie.backend.domain.email.AuthType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "비밀번호 변경 요청 DTO")
public record PasswordChangeRequest(
        @Schema(
                description = "비밀번호를 변경할 대상 사용자의 이메일 주소",
                example = "user@example.com",
                required = true
        )
        String email,

        @Schema(
                description = """
                        이메일 인증 유형  
                        
                        사용 용도:
                        - EMAIL_AUTH : 회원가입/이메일 인증  
                        - FIND_USER_ID_AUTH : 아이디 찾기  
                        - PW_AUTH : 비밀번호 재설정  
                        """,
                example = "PW_AUTH",
                required = true,
                implementation = AuthType.class
        )
        AuthType authType,

        @Schema(
                description = "새로 설정할 비밀번호",
                example = "newStrongPassword123!",
                required = true
        )
        String password
) {
}
