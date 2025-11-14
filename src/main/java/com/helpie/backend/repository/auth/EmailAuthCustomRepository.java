package com.helpie.backend.repository.auth;



import com.helpie.backend.domain.email.AuthType;
import com.helpie.backend.domain.email.EmailAuth;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailAuthCustomRepository {
    Optional<EmailAuth> findValidAuthByEmail(String email, AuthType authType, Integer authNumber, LocalDateTime currentTime);
}
