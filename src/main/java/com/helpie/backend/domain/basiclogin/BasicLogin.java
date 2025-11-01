package com.helpie.backend.domain.basiclogin;


import com.helpie.backend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BasicLogin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "lock_status", nullable = false)
    private LockStatus lockStatus;

    @Column(name = "login_fail_count", nullable = false)
    private Integer loginFailCount;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

