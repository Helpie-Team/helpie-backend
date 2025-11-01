package com.helpie.backend.repository.basiclogin;

import com.helpie.backend.domain.basiclogin.BasicLogin;
import com.helpie.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface BasicLoginRepository extends JpaRepository<BasicLogin, Long> {
    BasicLogin findByUserId(long userId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE BasicLogin b SET b.loginFailCount = b.loginFailCount + 1 WHERE b.user.id = :userId")
    void increaseLoginFailCount(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE BasicLogin b SET b.loginFailCount = 0, b.lockStatus = 'UNLOCK', b.lockedAt = NULL WHERE b.user.id = :userId")
    void resetLoginFailInfo(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE BasicLogin b SET b.lockStatus = 'LOCK', b.lockedAt = :lockedAt WHERE b.user.id = :userId")
    void lockUser(@Param("userId") Long userId, @Param("lockedAt") LocalDateTime lockedAt);
}
