package com.helpie.backend.repository.auth;

import com.helpie.backend.domain.email.AuthType;
import com.helpie.backend.domain.email.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long>, EmailAuthCustomRepository {
    Boolean existsByEmailAndAuthType(String email, AuthType authType);
}
