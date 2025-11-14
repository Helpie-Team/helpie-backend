package com.helpie.backend.domain.email;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class EmailAuth {

    private static final long MAX_EXPIRE_TIME = 5L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private AuthType authType;

    private Integer authNumber;

    private Boolean expired;

    private LocalDateTime expireDate;

    protected EmailAuth() {

    }

    @Builder
    private EmailAuth(String email, AuthType authType, Integer authNumber, Boolean expired) {
        this.email = email;
        this.authType = authType;
        this.authNumber = authNumber;
        this.expired = expired;
        this.expireDate = LocalDateTime.now().plusMinutes(MAX_EXPIRE_TIME);
    }

    public void expired() {
        this.expired = true;
    }
}
