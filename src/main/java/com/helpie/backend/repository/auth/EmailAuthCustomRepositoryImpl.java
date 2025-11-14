package com.helpie.backend.repository.auth;

import com.helpie.backend.domain.email.AuthType;
import com.helpie.backend.domain.email.EmailAuth;
import com.helpie.backend.domain.email.QEmailAuth;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

public class EmailAuthCustomRepositoryImpl implements EmailAuthCustomRepository {

    JPAQueryFactory jpaQueryFactory;

    public EmailAuthCustomRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    public Optional<EmailAuth> findValidAuthByEmail(String email, AuthType authType, Integer authNumber, LocalDateTime currentTime) {
        EmailAuth emailAuth = jpaQueryFactory
                .selectFrom(QEmailAuth.emailAuth)
                .where(QEmailAuth.emailAuth.email.eq(email),
                        QEmailAuth.emailAuth.authType.eq(authType),
                        QEmailAuth.emailAuth.authNumber.eq(authNumber),
                        QEmailAuth.emailAuth.expireDate.goe(currentTime),
                        QEmailAuth.emailAuth.expired.eq(false))
                .fetchFirst();

        return Optional.ofNullable(emailAuth);
    }

}
