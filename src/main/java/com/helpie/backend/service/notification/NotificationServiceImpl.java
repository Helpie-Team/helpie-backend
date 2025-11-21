package com.helpie.backend.service.notification;

import com.helpie.backend.domain.notification.Notification;
import com.helpie.backend.dto.notification.NotificationResponse;
import com.helpie.backend.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 알림 서비스 구현체
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    
    @Override
    @Transactional
    public void sendCommentNotification(Long receiverId, Long communityId, String communityTitle, Long actorId, String actorName) {
        log.debug("댓글 알림 발송 - receiverId: {}, communityId: {}, actorId: {}", receiverId, communityId, actorId);
        
        Notification notification = Notification.createCommentNotification(
            receiverId, communityId, actorId, actorName, communityTitle
        );
        
        notificationRepository.save(notification);
        log.info("댓글 알림 저장 완료 - notificationId: {}", notification.getId());
    }

    @Override
    @Transactional
    public void sendLikeNotification(Long receiverId, Long communityId, String communityTitle, Long actorId, String actorName) {
        log.debug("좋아요 알림 발송 - receiverId: {}, communityId: {}, actorId: {}", receiverId, communityId, actorId);
        
        Notification notification = Notification.createLikeNotification(
            receiverId, communityId, actorId, actorName, communityTitle
        );
        
        notificationRepository.save(notification);
        log.info("좋아요 알림 저장 완료 - notificationId: {}", notification.getId());
    }


    @Override
    public Page<NotificationResponse> getNotifications(Long userId, Pageable pageable) {
        log.debug("알림 목록 조회 - userId: {}", userId);
        
        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return notifications.map(NotificationResponse::from);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        log.debug("읽지 않은 알림 개수 조회 - userId: {}", userId);
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        log.debug("알림 읽음 처리 - notificationId: {}, userId: {}", notificationId, userId);
        
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다: " + notificationId));
        
        // 알림 소유자 확인
        if (!notification.getUserId().equals(userId)) {
            throw new IllegalArgumentException("알림을 읽을 권한이 없습니다");
        }
        
        if (!notification.isRead()) {
            notification.markAsRead();
            notificationRepository.save(notification);
            log.info("알림 읽음 처리 완료 - notificationId: {}", notificationId);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        log.debug("모든 알림 읽음 처리 - userId: {}", userId);
        
        notificationRepository.markAllAsReadByUserId(userId);
        log.info("모든 알림 읽음 처리 완료 - userId: {}", userId);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        log.debug("알림 삭제 - notificationId: {}, userId: {}", notificationId, userId);
        
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다: " + notificationId));
        
        // 알림 소유자 확인
        if (!notification.getUserId().equals(userId)) {
            throw new IllegalArgumentException("알림을 삭제할 권한이 없습니다");
        }
        
        notificationRepository.delete(notification);
        log.info("알림 삭제 완료 - notificationId: {}", notificationId);
    }

    @Override
    @Transactional
    public void deleteAllNotifications(Long userId) {
        log.debug("모든 알림 삭제 - userId: {}", userId);
        
        notificationRepository.deleteByUserId(userId);
        log.info("모든 알림 삭제 완료 - userId: {}", userId);
    }
}