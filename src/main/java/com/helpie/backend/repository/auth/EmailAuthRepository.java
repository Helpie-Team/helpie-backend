package com.helpie.backend.repository.auth;

import com.helpie.backend.domain.Email.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long>, EmailAuthCustomRepository {

}
