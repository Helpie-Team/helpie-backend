package com.helpie.backend.service.websocket;

import com.helpie.backend.dto.websocket.ChatWebSocketMessage;
import com.helpie.backend.dto.websocket.ChatMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 성능 최적화된 WebSocket 서비스
 * 배치 처리, 압축, 비동기 전송을 통해 성능을 최적화합니다.
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OptimizedWebSocketService {
    
    private final MessageBatchService messageBatchService;
    private final MessageCompressionService compressionService;
    private final ChatWebSocketService chatWebSocketService;
    private final WebSocketSessionManager sessionManager;
    
    /**
     * 최적화된 채팅 메시지 전송
     */
    @Async("websocketTaskExecutor")
    public CompletableFuture<Void> sendOptimizedChatMessage(ChatMessageRequest request) {
        return CompletableFuture.runAsync(() -> {
            try {
                // 기존 서비스로 DB 저장 및 검증
                chatWebSocketService.sendChatMessage(request);
                
                log.debug("최적화된 채팅 메시지 처리 완료 - 채팅방: {}, 사용자: {}", 
                    request.getChatRoomId(), request.getSenderId());
                
            } catch (Exception e) {
                log.error("최적화된 채팅 메시지 처리 실패 - 채팅방: {}, 사용자: {}", 
                    request.getChatRoomId(), request.getSenderId(), e);
                throw new RuntimeException(e);
            }
        });
    }
    
    /**
     * 최적화된 입장 메시지 전송
     */
    @Async("websocketTaskExecutor")
    public CompletableFuture<Void> sendOptimizedJoinMessage(ChatMessageRequest request) {
        return CompletableFuture.runAsync(() -> {
            try {
                // 기존 서비스로 DB 저장 및 검증
                chatWebSocketService.sendJoinMessage(request);
                
                log.debug("최적화된 입장 메시지 처리 완료 - 채팅방: {}, 사용자: {}", 
                    request.getChatRoomId(), request.getSenderName());
                
            } catch (Exception e) {
                log.error("최적화된 입장 메시지 처리 실패 - 채팅방: {}, 사용자: {}", 
                    request.getChatRoomId(), request.getSenderName(), e);
                throw new RuntimeException(e);
            }
        });
    }
    
    /**
     * 최적화된 퇴장 메시지 전송
     */
    @Async("websocketTaskExecutor")
    public CompletableFuture<Void> sendOptimizedLeaveMessage(ChatMessageRequest request) {
        return CompletableFuture.runAsync(() -> {
            try {
                // 기존 서비스로 DB 저장 및 검증
                chatWebSocketService.sendLeaveMessage(request);
                
                log.debug("최적화된 퇴장 메시지 처리 완료 - 채팅방: {}, 사용자: {}", 
                    request.getChatRoomId(), request.getSenderName());
                
            } catch (Exception e) {
                log.error("최적화된 퇴장 메시지 처리 실패 - 채팅방: {}, 사용자: {}", 
                    request.getChatRoomId(), request.getSenderName(), e);
                throw new RuntimeException(e);
            }
        });
    }
    
    /**
     * 배치 처리를 통한 메시지 전송
     */
    public void sendMessageWithBatch(Long chatRoomId, ChatWebSocketMessage message) {
        // 메시지 압축 체크
        if (shouldCompressMessage(message)) {
            MessageCompressionService.CompressedMessage compressed = 
                compressionService.compressMessage(message);
            
            ChatWebSocketMessage compressedMessage = createCompressedMessage(message, compressed);
            messageBatchService.addToBatch(chatRoomId, compressedMessage);
        } else {
            messageBatchService.addToBatch(chatRoomId, message);
        }
    }
    
    /**
     * 즉시 전송 (중요한 메시지용)
     */
    public void sendMessageImmediately(Long chatRoomId, ChatWebSocketMessage message) {
        messageBatchService.sendImmediately(chatRoomId, message);
    }
    
    /**
     * 온라인 사용자에게만 최적화된 메시지 전송
     */
    @Async("websocketTaskExecutor")
    public CompletableFuture<Void> sendToOnlineUsers(Long chatRoomId, ChatWebSocketMessage message) {
        return CompletableFuture.runAsync(() -> {
            try {
                // 온라인 사용자 수 확인
                int onlineUserCount = sessionManager.getOnlineUserCountInChatRoom(chatRoomId);
                
                if (onlineUserCount == 0) {
                    log.debug("온라인 사용자가 없어 메시지 전송을 건너뜁니다 - 채팅방: {}", chatRoomId);
                    return;
                }
                
                // 온라인 사용자가 있을 때만 전송
                if (onlineUserCount >= 3) {
                    // 사용자가 많으면 배치 처리
                    sendMessageWithBatch(chatRoomId, message);
                } else {
                    // 사용자가 적으면 즉시 전송
                    sendMessageImmediately(chatRoomId, message);
                }
                
                log.debug("온라인 사용자 기반 메시지 전송 완료 - 채팅방: {}, 온라인 사용자: {}", 
                    chatRoomId, onlineUserCount);
                
            } catch (Exception e) {
                log.error("온라인 사용자 기반 메시지 전송 실패 - 채팅방: {}", chatRoomId, e);
            }
        });
    }
    
    /**
     * 메시지 압축 필요성 판단
     */
    private boolean shouldCompressMessage(ChatWebSocketMessage message) {
        if (message.getContent() == null) {
            return false;
        }
        
        // 긴 텍스트 메시지만 압축
        return message.getContent().length() > 200;
    }
    
    /**
     * 압축된 메시지 생성
     */
    private ChatWebSocketMessage createCompressedMessage(ChatWebSocketMessage original, 
                                                        MessageCompressionService.CompressedMessage compressed) {
        return new ChatWebSocketMessage(
            original.getType(),
            original.getChatRoomId(),
            original.getSenderId(),
            original.getSenderName(),
            original.getSenderProfileImage(),
            compressed.getData(),
            original.getTimestamp()
        );
    }
    
}