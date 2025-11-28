package com.helpie.backend.service.notification;

import com.helpie.backend.domain.notification.NotificationSetting;
import com.helpie.backend.dto.notification.NotificationSettingResponse;
import com.helpie.backend.dto.notification.NotificationSettingUpdateRequest;
import com.helpie.backend.repository.notification.NotificationSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 알림 설정 서비스 구현체
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationSettingServiceImpl implements NotificationSettingService {

    private final NotificationSettingRepository notificationSettingRepository;

    @Override
    public NotificationSettingResponse getNotificationSettings(Long userId) {
        log.debug("알림 설정 조회 - userId: {}", userId);
        
        NotificationSetting setting = notificationSettingRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultSetting(userId));
            
        return NotificationSettingResponse.from(setting);
    }

    @Override
    @Transactional
    public NotificationSettingResponse updateNotificationSettings(Long userId, NotificationSettingUpdateRequest request) {
        log.debug("알림 설정 변경 - userId: {}, request: {}", userId, request);
        
        NotificationSetting setting = notificationSettingRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultSetting(userId));
            
        // 전체 알림 설정 변경
        setting.updateAllNotifications(request.allNotifications());
        
        // 개별 알림 설정 변경
        setting.updateNotificationSettings(
            request.commentNotifications(), 
            request.likeNotifications()
        );
        
        NotificationSetting savedSetting = notificationSettingRepository.save(setting);
        log.info("알림 설정 변경 완료 - userId: {}", userId);
        
        return NotificationSettingResponse.from(savedSetting);
    }

    @Override
    public boolean canReceiveCommentNotification(Long userId) {
        NotificationSetting setting = notificationSettingRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultSetting(userId));
        return setting.canReceiveCommentNotification();
    }

    @Override
    public boolean canReceiveLikeNotification(Long userId) {
        NotificationSetting setting = notificationSettingRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultSetting(userId));
        return setting.canReceiveLikeNotification();
    }


    /**
     * 기본 알림 설정 생성 (자동 저장)
     */
    @Transactional
    private NotificationSetting createDefaultSetting(Long userId) {
        log.debug("기본 알림 설정 생성 - userId: {}", userId);
        
        NotificationSetting defaultSetting = NotificationSetting.createDefault(userId);
        NotificationSetting savedSetting = notificationSettingRepository.save(defaultSetting);
        
        log.info("기본 알림 설정 생성 완료 - userId: {}", userId);
        return savedSetting;
    }
}