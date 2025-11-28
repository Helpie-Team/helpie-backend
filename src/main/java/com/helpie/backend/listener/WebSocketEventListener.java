package com.helpie.backend.listener;

import com.helpie.backend.service.websocket.ChatWebSocketService;
import com.helpie.backend.service.websocket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

/**
 * 웹소켓 연결 이벤트 리스너
 * 사용자의 연결/해제를 모니터링합니다.
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    
    private final ChatWebSocketService webSocketService;
    private final WebSocketSessionManager sessionManager;
    
    /**
     * WebSocket 연결 시도 이벤트 처리 (인증 전)
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        log.info("🟡 WebSocket 연결 시도 - SessionID: {}", sessionId);
    }
    
    /**
     * WebSocket 연결 완료 이벤트 처리 (인증 후)
     */
    @EventListener
    public void handleWebSocketConnectedListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        // 사용자 정보 추출
        Object userIdObj = headerAccessor.getSessionAttributes().get("userId");
        Object userNameObj = headerAccessor.getSessionAttributes().get("userName");
        
        if (userIdObj != null && userNameObj != null) {
            Long userId = (Long) userIdObj;
            String userName = (String) userNameObj;
            
            log.info("🟢 WebSocket 연결 성공! SessionID: {}, 사용자: {}({})", 
                    sessionId, userName, userId);
            log.info("📊 현재 활성 세션 수: {}", sessionManager.getActiveSessionCount());
        } else {
            log.info("🟢 WebSocket 연결 완료 (사용자 정보 없음) - SessionID: {}", sessionId);
        }
    }
    
    /**
     * 토픽 구독 이벤트 처리
     */
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        String sessionId = headerAccessor.getSessionId();
        
        // 사용자 정보 추출
        Object userIdObj = headerAccessor.getSessionAttributes().get("userId");
        Object userNameObj = headerAccessor.getSessionAttributes().get("userName");
        
        if (userIdObj != null && userNameObj != null && destination != null) {
            String userName = (String) userNameObj;
            Long userId = (Long) userIdObj;
            
            // 채팅방 ID 추출 (예: /topic/chatroom/123 -> 123)
            if (destination.startsWith("/topic/chatroom/")) {
                String chatRoomId = destination.substring("/topic/chatroom/".length());
                log.info("📺 채팅방 구독! 사용자: {}({}) -> 채팅방: {} (SessionID: {})", 
                        userName, userId, chatRoomId, sessionId);
            } else {
                log.info("📺 토픽 구독! 사용자: {}({}) -> {} (SessionID: {})", 
                        userName, userId, destination, sessionId);
            }
        } else {
            log.info("📺 토픽 구독 - Destination: {} (SessionID: {})", destination, sessionId);
        }
        
        // 채팅방 구독 시 세션 활동 시간 업데이트
        if (destination != null && destination.startsWith("/topic/chatroom/")) {
            sessionManager.updateLastActivity(sessionId);
        }
    }
    
    /**
     * 토픽 구독 해제 이벤트 처리
     */
    @EventListener
    public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String subscriptionId = headerAccessor.getSubscriptionId();
        
        // 사용자 정보 추출
        Object userIdObj = headerAccessor.getSessionAttributes().get("userId");
        Object userNameObj = headerAccessor.getSessionAttributes().get("userName");
        
        if (userIdObj != null && userNameObj != null) {
            String userName = (String) userNameObj;
            Long userId = (Long) userIdObj;
            
            log.info("📺❌ 구독 해제! 사용자: {}({}) - 구독ID: {} (SessionID: {})", 
                    userName, userId, subscriptionId, sessionId);
        } else {
            log.info("📺❌ 구독 해제 - 구독ID: {} (SessionID: {})", subscriptionId, sessionId);
        }
        
        // 세션 활동 시간 업데이트
        sessionManager.updateLastActivity(sessionId);
    }
    
    /**
     * WebSocket 연결 해제 이벤트 처리
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = event.getSessionId();
        
        // 세션 매니저에서 사용자 정보 조회
        WebSocketSessionManager.UserSession session = sessionManager.getSession(sessionId);
        
        if (session != null) {
            log.info("🔴 WebSocket 연결 해제! SessionID: {}, 사용자: {}({})", 
                    sessionId, session.getUserName(), session.getUserId());
            
            // 자동 퇴장 메시지 제거 (UX 개선: 페이지 전환 시 불필요한 알림 방지)
            
            // 세션 제거
            sessionManager.removeSession(sessionId);
            
            log.info("📊 현재 활성 세션 수: {}", sessionManager.getActiveSessionCount());
            if (session.getChatRoomId() != null) {
                log.info("🏠 채팅방 {}의 온라인 사용자 수: {}", 
                        session.getChatRoomId(), 
                        sessionManager.getOnlineUserCountInChatRoom(session.getChatRoomId()));
            }
        } else {
            log.info("🔴 WebSocket 연결 해제 - SessionID: {} (세션 정보 없음)", sessionId);
        }
    }
}