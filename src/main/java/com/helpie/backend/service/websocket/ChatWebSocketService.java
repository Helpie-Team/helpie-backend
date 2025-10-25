package com.helpie.backend.service.websocket;

import com.helpie.backend.dto.websocket.ChatMessageRequest;

/**
 * 웹소켓 채팅 서비스 인터페이스
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
public interface ChatWebSocketService {
    
    /**
     * 채팅 메시지를 전송합니다.
     * 
     * @param request 메시지 요청
     */
    void sendChatMessage(ChatMessageRequest request);
    
    /**
     * 입장 메시지를 전송합니다.
     * 
     * @param request 입장 요청
     */
    void sendJoinMessage(ChatMessageRequest request);
    
    /**
     * 퇴장 메시지를 전송합니다.
     * 
     * @param request 퇴장 요청
     */
    void sendLeaveMessage(ChatMessageRequest request);
    
    /**
     * 시스템 메시지를 전송합니다.
     * 
     * @param chatRoomId 채팅방 ID
     * @param content 메시지 내용
     */
    void sendSystemMessage(Long chatRoomId, String content);
}