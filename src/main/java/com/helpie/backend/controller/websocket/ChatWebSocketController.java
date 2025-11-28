package com.helpie.backend.controller.websocket;

import com.helpie.backend.dto.websocket.ChatWebSocketMessage;
import com.helpie.backend.dto.websocket.ChatMessageRequest;
import com.helpie.backend.dto.websocket.WebSocketErrorMessage;
import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.WebSocketException;
import com.helpie.backend.service.websocket.ChatWebSocketService;
import com.helpie.backend.service.websocket.WebSocketSessionManager;
import com.helpie.backend.service.websocket.OptimizedWebSocketService;
import com.helpie.backend.service.auth.ChatRoomAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

/**
 * 웹소켓 채팅 컨트롤러
 * 실시간 채팅 메시지 처리
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    
    private final ChatWebSocketService chatWebSocketService;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionManager sessionManager;
    private final ChatRoomAuthService chatRoomAuthService;
    private final OptimizedWebSocketService optimizedWebSocketService;
    
    /**
     * 채팅 메시지 전송
     * 경로: /app/chat/{chatRoomId}
     */
    @MessageMapping("/chat/{chatRoomId}")
    public void sendMessage(
        @DestinationVariable Long chatRoomId,
        @Payload ChatMessageRequest request,
        SimpMessageHeaderAccessor headerAccessor
    ) {
        try {
            // 사용자 인증 확인
            validateAuthentication(headerAccessor);
            
            // 입력 데이터 검증
            validateMessageRequest(request, chatRoomId);
            
            // 인증된 사용자 정보로 요청 데이터 보정
            Long authenticatedUserId = (Long) headerAccessor.getSessionAttributes().get("userId");
            String authenticatedUsername = (String) headerAccessor.getSessionAttributes().get("userName");
            request.setSenderId(authenticatedUserId);
            request.setSenderName(authenticatedUsername);
            
            // 채팅방 접근 권한 검증
            chatRoomAuthService.validateChatRoomAccess(chatRoomId, authenticatedUserId);
            
            log.info("웹소켓 메시지 수신 - 채팅방: {}, 사용자: {}, 내용: {}", 
                chatRoomId, request.getSenderId(), request.getContent());
            
            // 세션 활동 시간 업데이트
            sessionManager.updateLastActivity(headerAccessor.getSessionId());
            
            // 채팅방 ID 설정
            request.setChatRoomId(chatRoomId);
            
            // 최적화된 메시지 처리 및 브로드캐스트
            optimizedWebSocketService.sendOptimizedChatMessage(request);
            
        } catch (WebSocketException e) {
            log.warn("웹소켓 메시지 처리 중 비즈니스 오류: {}", e.getMessage());
            sendErrorToUser(headerAccessor.getSessionId(), e.getErrorCode(), e.getMessage());
        } catch (BusinessException e) {
            log.warn("웹소켓 메시지 처리 중 비즈니스 오류: {}", e.getMessage());
            sendErrorToUser(headerAccessor.getSessionId(), e.getErrorCode().getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("웹소켓 메시지 처리 중 예상치 못한 오류 발생", e);
            sendErrorToUser(headerAccessor.getSessionId(), "WS_INTERNAL_ERROR", "메시지 전송 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 채팅방 입장
     * 경로: /app/chat/{chatRoomId}/join
     */
    @MessageMapping("/chat/{chatRoomId}/join")
    public void joinChatRoom(
        @DestinationVariable Long chatRoomId,
        @Payload ChatMessageRequest request,
        SimpMessageHeaderAccessor headerAccessor
    ) {
        try {
            // 사용자 인증 확인
            validateAuthentication(headerAccessor);
            
            // 인증된 사용자 정보로 요청 데이터 보정
            Long authenticatedUserId = (Long) headerAccessor.getSessionAttributes().get("userId");
            String authenticatedUsername = (String) headerAccessor.getSessionAttributes().get("userName");
            request.setSenderId(authenticatedUserId);
            request.setSenderName(authenticatedUsername);
            
            // 입력 데이터 검증
            validateJoinRequest(request, chatRoomId);
            
            // 채팅방 접근 권한 검증
            chatRoomAuthService.validateChatRoomAccess(chatRoomId, authenticatedUserId);
            
            log.info("웹소켓 입장 - 채팅방: {}, 사용자: {}", chatRoomId, request.getSenderId());
            
            // 채팅방 ID 설정
            request.setChatRoomId(chatRoomId);
            
            // 세션 매니저에 사용자 세션 등록
            sessionManager.registerSession(
                headerAccessor.getSessionId(),
                request.getSenderId(),
                request.getSenderName(),
                chatRoomId
            );
            
            // 세션에 사용자 정보 저장 (기존 호환성 유지)
            headerAccessor.getSessionAttributes().put("userId", request.getSenderId());
            headerAccessor.getSessionAttributes().put("userName", request.getSenderName());
            headerAccessor.getSessionAttributes().put("chatRoomId", chatRoomId);
            
            // 최적화된 입장 메시지 브로드캐스트
            optimizedWebSocketService.sendOptimizedJoinMessage(request);
            
        } catch (WebSocketException e) {
            log.warn("웹소켓 입장 처리 중 비즈니스 오류: {}", e.getMessage());
            sendErrorToUser(headerAccessor.getSessionId(), e.getErrorCode(), e.getMessage());
        } catch (BusinessException e) {
            log.warn("웹소켓 입장 처리 중 비즈니스 오류: {}", e.getMessage());
            sendErrorToUser(headerAccessor.getSessionId(), e.getErrorCode().getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("웹소켓 입장 처리 중 예상치 못한 오류 발생", e);
            sendErrorToUser(headerAccessor.getSessionId(), "WS_INTERNAL_ERROR", "채팅방 입장 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 채팅방 퇴장 (조용한 퇴장)
     * 경로: /app/chat/{chatRoomId}/leave
     * 퇴장 알림 메시지는 UX 개선을 위해 전송되지 않습니다
     */
    @MessageMapping("/chat/{chatRoomId}/leave")
    public void leaveChatRoom(
        @DestinationVariable Long chatRoomId,
        @Payload ChatMessageRequest request,
        SimpMessageHeaderAccessor headerAccessor
    ) {
        try {
            // 사용자 인증 확인
            validateAuthentication(headerAccessor);
            
            // 인증된 사용자 정보로 요청 데이터 보정
            Long authenticatedUserId = (Long) headerAccessor.getSessionAttributes().get("userId");
            String authenticatedUsername = (String) headerAccessor.getSessionAttributes().get("userName");
            request.setSenderId(authenticatedUserId);
            request.setSenderName(authenticatedUsername);
            
            // 입력 데이터 검증
            validateLeaveRequest(request, chatRoomId);
            
            // 채팅방 접근 권한 검증 (퇴장 시에도 권한 확인)
            chatRoomAuthService.validateChatRoomAccess(chatRoomId, authenticatedUserId);
            
            log.info("웹소켓 퇴장 - 채팅방: {}, 사용자: {}", chatRoomId, request.getSenderId());
            
            // 채팅방 ID 설정
            request.setChatRoomId(chatRoomId);
            
            // 조용한 퇴장 처리 (메시지 브로드캐스트 없음)
            optimizedWebSocketService.sendOptimizedLeaveMessage(request);
            
            // 세션 매니저에서 사용자 세션 제거
            sessionManager.removeSession(headerAccessor.getSessionId());
            
            // 세션 정보 정리
            headerAccessor.getSessionAttributes().clear();
            
        } catch (WebSocketException e) {
            log.warn("웹소켓 퇴장 처리 중 비즈니스 오류: {}", e.getMessage());
            sendErrorToUser(headerAccessor.getSessionId(), e.getErrorCode(), e.getMessage());
        } catch (BusinessException e) {
            log.warn("웹소켓 퇴장 처리 중 비즈니스 오류: {}", e.getMessage());
            sendErrorToUser(headerAccessor.getSessionId(), e.getErrorCode().getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("웹소켓 퇴장 처리 중 예상치 못한 오류 발생", e);
            sendErrorToUser(headerAccessor.getSessionId(), "WS_INTERNAL_ERROR", "채팅방 퇴장 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 웹소켓 예외 처리 핸들러
     */
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public WebSocketErrorMessage handleException(Exception exception) {
        log.error("웹소켓 전역 예외 발생", exception);
        
        if (exception instanceof WebSocketException) {
            WebSocketException wsEx = (WebSocketException) exception;
            return WebSocketErrorMessage.of(wsEx.getErrorCode(), wsEx.getMessage());
        } else if (exception instanceof BusinessException) {
            BusinessException bizEx = (BusinessException) exception;
            return WebSocketErrorMessage.of(bizEx.getErrorCode().getCode(), bizEx.getMessage());
        } else {
            return WebSocketErrorMessage.of("WS_UNKNOWN_ERROR", "알 수 없는 오류가 발생했습니다.");
        }
    }
    
    /**
     * 사용자 인증 확인
     */
    private void validateAuthentication(SimpMessageHeaderAccessor headerAccessor) {
        Boolean authenticated = (Boolean) headerAccessor.getSessionAttributes().get("authenticated");
        if (authenticated == null || !authenticated) {
            throw WebSocketException.accessDenied("인증되지 않은 사용자입니다");
        }
        
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        String username = (String) headerAccessor.getSessionAttributes().get("userName");
        
        if (userId == null || username == null) {
            throw WebSocketException.accessDenied("인증 정보가 부족합니다");
        }
    }
    
    /**
     * 메시지 요청 데이터 검증
     */
    private void validateMessageRequest(ChatMessageRequest request, Long chatRoomId) {
        if (request == null) {
            throw WebSocketException.invalidMessage("요청 데이터가 없습니다");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw WebSocketException.invalidMessage("메시지 내용이 필요합니다");
        }
        if (request.getContent().length() > 1000) {
            throw WebSocketException.invalidMessage("메시지는 1000자 이내로 입력해주세요");
        }
    }
    
    /**
     * 입장 요청 데이터 검증
     */
    private void validateJoinRequest(ChatMessageRequest request, Long chatRoomId) {
        if (request == null) {
            throw WebSocketException.invalidMessage("요청 데이터가 없습니다");
        }
        // 인증된 사용자 정보가 이미 설정되어 있으므로 추가 검증 불필요
    }
    
    /**
     * 퇴장 요청 데이터 검증
     */
    private void validateLeaveRequest(ChatMessageRequest request, Long chatRoomId) {
        if (request == null) {
            throw WebSocketException.invalidMessage("요청 데이터가 없습니다");
        }
        // 인증된 사용자 정보가 이미 설정되어 있으므로 추가 검증 불필요
    }
    
    /**
     * 특정 사용자에게 에러 메시지 전송
     */
    private void sendErrorToUser(String sessionId, String errorCode, String message) {
        try {
            WebSocketErrorMessage errorMessage = WebSocketErrorMessage.of(errorCode, message);
            messagingTemplate.convertAndSendToUser(
                sessionId, 
                "/queue/errors", 
                errorMessage
            );
        } catch (Exception e) {
            log.error("에러 메시지 전송 실패: sessionId={}, errorCode={}", sessionId, errorCode, e);
        }
    }
}