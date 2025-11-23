package com.helpie.backend.controller.notification;

import com.helpie.backend.domain.user.UserRole;
import com.helpie.backend.domain.user.UserVo;
import com.helpie.backend.dto.notification.NotificationSettingResponse;
import com.helpie.backend.dto.notification.NotificationSettingUpdateRequest;
import com.helpie.backend.dto.notification.NotificationResponse;
import com.helpie.backend.service.notification.NotificationSettingService;
import com.helpie.backend.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 알림 컨트롤러
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "알림 관리", description = """
    알림 설정 및 알림 목록 관리 API
    
    **실시간 알림:**
    - WebSocket 연결: /ws/notifications
    - 개별 알림 구독: /topic/notifications/{userId}
    - 알림 개수 구독: /topic/notifications/{userId}/count
    
    **REST API:**
    - 기존 방식과 병행하여 사용 가능
    - 알림 목록 조회, 읽음/삭제 처리
    """)
public class NotificationController {

    private final NotificationSettingService notificationSettingService;
    private final NotificationService notificationService;

    @GetMapping("/settings")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(
        summary = "알림 설정 조회", 
        description = "사용자의 알림 설정을 조회합니다. 설정이 없는 경우 기본값을 반환합니다."
    )
    public ResponseEntity<NotificationSettingResponse> getNotificationSettings(
        @AuthenticationPrincipal UserVo userVo
    ) {
        NotificationSettingResponse response = notificationSettingService.getNotificationSettings(userVo.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/settings")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(
        summary = "알림 설정 변경",
        description = """
                     사용자의 알림 설정을 변경합니다.
                     
                     **설정 항목:**
                     - allNotifications: 전체 알림 (false인 경우 모든 알림 비활성화)
                     - commentNotifications: 댓글 알림 (내 게시글에 댓글)
                     - likeNotifications: 좋아요 알림 (내 게시글에 좋아요)
                     """
    )
    public ResponseEntity<NotificationSettingResponse> updateNotificationSettings(
        @AuthenticationPrincipal UserVo userVo,
        @Valid @RequestBody NotificationSettingUpdateRequest request
    ) {
        NotificationSettingResponse response = notificationSettingService.updateNotificationSettings(
            userVo.getId(), request
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(
        summary = "알림 목록 조회",
        description = """
                     사용자의 알림 목록을 최신순으로 조회합니다.
                     
                     **실시간 알림과 병행 사용:**
                     - WebSocket으로 실시간 수신: /topic/notifications/{userId}
                     - REST API로 기존 알림 조회: 이 API 사용
                     - 두 방식 모두 동일한 데이터 구조 사용
                     """
    )
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
        @AuthenticationPrincipal UserVo userVo,
        @Parameter(description = "페이징 정보 (기본: 20개, 최신순)")
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<NotificationResponse> response = notificationService.getNotifications(userVo.getId(), pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/unread-count")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(
        summary = "읽지 않은 알림 개수 조회",
        description = "사용자의 읽지 않은 알림 개수를 조회합니다. 벨 아이콘에 표시될 숫자입니다."
    )
    public ResponseEntity<Long> getUnreadCount(
        @AuthenticationPrincipal UserVo userVo
    ) {
        Long count = notificationService.getUnreadCount(userVo.getId());
        return ResponseEntity.ok(count);
    }
    
    @PutMapping("/{notificationId}/read")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(
        summary = "알림 읽음 처리",
        description = "특정 알림을 읽음으로 처리합니다."
    )
    public ResponseEntity<Void> markAsRead(
        @AuthenticationPrincipal UserVo userVo,
        @Parameter(description = "알림 ID") @PathVariable Long notificationId
    ) {
        notificationService.markAsRead(notificationId, userVo.getId());
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/read-all")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(
        summary = "모든 알림 읽음 처리",
        description = "사용자의 모든 읽지 않은 알림을 읽음으로 처리합니다."
    )
    public ResponseEntity<Void> markAllAsRead(
        @AuthenticationPrincipal UserVo userVo
    ) {
        notificationService.markAllAsRead(userVo.getId());
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{notificationId}")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(
        summary = "알림 삭제",
        description = "특정 알림을 삭제합니다. 이미지의 X 버튼 기능입니다."
    )
    public ResponseEntity<Void> deleteNotification(
        @AuthenticationPrincipal UserVo userVo,
        @Parameter(description = "알림 ID") @PathVariable Long notificationId
    ) {
        notificationService.deleteNotification(notificationId, userVo.getId());
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/all")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(
        summary = "모든 알림 삭제",
        description = "사용자의 모든 알림을 삭제합니다. '모두지우기' 버튼 기능입니다."
    )
    public ResponseEntity<Void> deleteAllNotifications(
        @AuthenticationPrincipal UserVo userVo
    ) {
        notificationService.deleteAllNotifications(userVo.getId());
        return ResponseEntity.ok().build();
    }
}