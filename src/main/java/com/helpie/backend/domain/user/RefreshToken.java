package com.helpie.backend.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @TODO: 추후 redis로 변경
 */
@Table(name = "refresh_tokens")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    private String token;

    @ManyToOne()
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    public RefreshToken(
            String token,
            User user,
            Duration expiredDuration
    ) {
        this.token = token;
        this.user = user;
        this.expiredAt = LocalDateTime.now().plus(expiredDuration);
    }
}
