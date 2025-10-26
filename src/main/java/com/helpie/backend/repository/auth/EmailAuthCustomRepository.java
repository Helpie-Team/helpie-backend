package com.helpie.backend.repository.auth;



import com.helpie.backend.domain.Email.AuthType;
import com.helpie.backend.domain.Email.EmailAuth;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailAuthCustomRepository {
    Optional<EmailAuth> findValidAuthByEmail(String email, AuthType authType, Integer authNumber, LocalDateTime currentTime);
}
