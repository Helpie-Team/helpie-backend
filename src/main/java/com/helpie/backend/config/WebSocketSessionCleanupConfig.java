package com.helpie.backend.config;

import com.helpie.backend.service.websocket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 웹소켓 세션 정리 스케줄링 설정
 * 비활성 세션을 주기적으로 정리합니다.
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class WebSocketSessionCleanupConfig {
    
    private final WebSocketSessionManager sessionManager;
    
    /**
     * 비활성 세션 정리 작업
     * 30분마다 실행하여 60분 이상 비활성 세션을 정리합니다.
     */
    @Scheduled(fixedRate = 30 * 60 * 1000) // 30분마다 실행
    public void cleanupInactiveSessions() {
        try {
            sessionManager.cleanupInactiveSessions(60); // 60분 이상 비활성 세션 정리
            log.debug("비활성 세션 정리 작업 완료");
        } catch (Exception e) {
            log.error("비활성 세션 정리 중 오류 발생", e);
        }
    }
    
}