package com.helpie.backend.repository.notification;

import com.helpie.backend.domain.notification.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 알림 설정 리포지토리
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
    
    /**
     * 사용자 ID로 알림 설정 조회
     */
    Optional<NotificationSetting> findByUserId(Long userId);
    
    /**
     * 사용자 ID로 알림 설정 존재 여부 확인
     */
    boolean existsByUserId(Long userId);
}