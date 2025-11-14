package com.helpie.backend.dto.auth;

import com.helpie.backend.domain.email.AuthType;

public record EmailAuthCheckRequest(
        String email,
        AuthType authType,
        Integer authNumber
) {
}
