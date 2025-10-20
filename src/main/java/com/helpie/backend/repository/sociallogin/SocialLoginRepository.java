package com.helpie.backend.repository.sociallogin;

import com.helpie.backend.domain.sociallogin.SocialLogin;
import com.helpie.backend.domain.sociallogin.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialLoginRepository extends JpaRepository<SocialLogin, Long> {
    boolean existsByCodeAndSocialType(String code, SocialType socialType);
    Optional<SocialLogin> findByCodeAndSocialType(String code, SocialType socialType);
}