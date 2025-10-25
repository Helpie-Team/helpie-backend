package com.helpie.backend.service.websocket;

import com.helpie.backend.dto.websocket.ChatWebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * 메시지 배치 처리 서비스
 * 여러 메시지를 모아서 효율적으로 전송하여 성능을 최적화합니다.
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageBatchService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    // 채팅방별 메시지 배치 큐
    private final ConcurrentMap<Long, ConcurrentLinkedQueue<ChatWebSocketMessage>> messageBatches = new ConcurrentHashMap<>();
    
    // 배치 크기 (한 번에 처리할 메시지 수)
    private static final int BATCH_SIZE = 10;
    
    // 배치 대기 시간 (밀리초)
    private static final int BATCH_TIMEOUT_MS = 100;
    
    /**
     * 메시지를 배치에 추가
     */
    public void addToBatch(Long chatRoomId, ChatWebSocketMessage message) {
        ConcurrentLinkedQueue<ChatWebSocketMessage> batch = messageBatches.computeIfAbsent(
            chatRoomId, 
            k -> new ConcurrentLinkedQueue<>()
        );
        
        batch.offer(message);
        
        // 배치 크기가 임계값에 도달하면 즉시 전송
        if (batch.size() >= BATCH_SIZE) {
            processBatch(chatRoomId);
        }
    }
    
    /**
     * 주기적으로 배치 처리 (100ms마다)
     */
    @Scheduled(fixedRate = BATCH_TIMEOUT_MS)
    public void processAllBatches() {
        for (Long chatRoomId : messageBatches.keySet()) {
            processBatch(chatRoomId);
        }
    }
    
    /**
     * 특정 채팅방의 배치 처리
     */
    @Async("websocketTaskExecutor")
    public void processBatch(Long chatRoomId) {
        ConcurrentLinkedQueue<ChatWebSocketMessage> batch = messageBatches.get(chatRoomId);
        if (batch == null || batch.isEmpty()) {
            return;
        }
        
        List<ChatWebSocketMessage> messages = new ArrayList<>();
        ChatWebSocketMessage message;
        
        // 배치에서 메시지들을 가져옴
        while ((message = batch.poll()) != null && messages.size() < BATCH_SIZE) {
            messages.add(message);
        }
        
        if (!messages.isEmpty()) {
            try {
                // 배치로 메시지 전송
                sendBatchMessages(chatRoomId, messages);
                
                log.debug("배치 메시지 전송 완료 - 채팅방: {}, 메시지 수: {}", chatRoomId, messages.size());
                
            } catch (Exception e) {
                log.error("배치 메시지 전송 실패 - 채팅방: {}", chatRoomId, e);
                
                // 실패한 메시지들을 다시 배치에 추가 (재시도)
                for (ChatWebSocketMessage failedMessage : messages) {
                    batch.offer(failedMessage);
                }
            }
        }
        
        // 배치가 비어있으면 맵에서 제거
        if (batch.isEmpty()) {
            messageBatches.remove(chatRoomId);
        }
    }
    
    /**
     * 배치 메시지들을 실제로 전송
     */
    private void sendBatchMessages(Long chatRoomId, List<ChatWebSocketMessage> messages) {
        String destination = "/topic/chatroom/" + chatRoomId;
        
        if (messages.size() == 1) {
            // 단일 메시지는 기존 방식으로 전송
            messagingTemplate.convertAndSend(destination, messages.get(0));
        } else {
            // 다중 메시지는 배치로 전송
            BatchWebSocketMessage batchMessage = new BatchWebSocketMessage(messages);
            messagingTemplate.convertAndSend(destination, batchMessage);
        }
    }
    
    /**
     * 즉시 메시지 전송 (배치 처리 없이)
     */
    @Async("websocketTaskExecutor")
    public void sendImmediately(Long chatRoomId, ChatWebSocketMessage message) {
        try {
            String destination = "/topic/chatroom/" + chatRoomId;
            messagingTemplate.convertAndSend(destination, message);
            
            log.debug("즉시 메시지 전송 완료 - 채팅방: {}", chatRoomId);
            
        } catch (Exception e) {
            log.error("즉시 메시지 전송 실패 - 채팅방: {}", chatRoomId, e);
        }
    }
    
    
    /**
     * 배치 메시지 DTO
     */
    public static class BatchWebSocketMessage {
        private final List<ChatWebSocketMessage> messages;
        private final long timestamp;
        private final String type = "BATCH";
        
        public BatchWebSocketMessage(List<ChatWebSocketMessage> messages) {
            this.messages = new ArrayList<>(messages);
            this.timestamp = System.currentTimeMillis();
        }
        
        public List<ChatWebSocketMessage> getMessages() {
            return messages;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public String getType() {
            return type;
        }
        
        public int getSize() {
            return messages.size();
        }
    }
}