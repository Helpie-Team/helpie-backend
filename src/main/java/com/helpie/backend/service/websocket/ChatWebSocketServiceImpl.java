package com.helpie.backend.service.websocket;

import com.helpie.backend.domain.chatroom.ChatRoom;
import com.helpie.backend.domain.chatroom.ChatMessage;
import com.helpie.backend.domain.chatroom.MessageType;
import com.helpie.backend.domain.group.GroupMember;
import com.helpie.backend.dto.websocket.ChatWebSocketMessage;
import com.helpie.backend.dto.websocket.ChatMessageRequest;
import com.helpie.backend.repository.chatroom.ChatRoomRepository;
import com.helpie.backend.repository.chatroom.ChatMessageRepository;
import com.helpie.backend.repository.group.GroupMemberRepository;
import com.helpie.backend.exception.chatroom.ChatRoomNotFoundException;
import com.helpie.backend.exception.WebSocketException;
import com.helpie.backend.service.user.UserImageService;
import com.helpie.backend.domain.user.UserImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 웹소켓 채팅 서비스 구현체
 * 실시간 메시지 전송 및 DB 저장을 담당합니다.
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatWebSocketServiceImpl implements ChatWebSocketService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository messageRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserImageService userImageService;
    
    @Override
    @Transactional
    public void sendChatMessage(ChatMessageRequest request) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
            .orElseThrow(() -> WebSocketException.chatRoomNotFound(request.getChatRoomId()));
        
        // 소모임 멤버 권한 확인
        validateGroupMembership(chatRoom.getGroup().getId(), request.getSenderId());
        
        // DB에 메시지 저장
        ChatMessage message = new ChatMessage(
            chatRoom,
            request.getSenderId(),
            request.getSenderName(),
            request.getContent()
        );
        messageRepository.save(message);
        
        // 발신자 프로필 이미지 조회
        String profileImage = userImageService.getUserImage(request.getSenderId())
            .map(UserImage::getImageUrl)
            .orElse(null);
        
        // 웹소켓으로 실시간 전송
        ChatWebSocketMessage wsMessage = ChatWebSocketMessage.createChatMessage(
            request.getChatRoomId(),
            request.getSenderId(),
            request.getSenderName(),
            profileImage,
            request.getContent()
        );
        
        messagingTemplate.convertAndSend(
            "/topic/chatroom/" + request.getChatRoomId(),
            wsMessage
        );
        
        log.info("채팅 메시지 전송 완료 - 채팅방: {}, 사용자: {}", 
            request.getChatRoomId(), request.getSenderId());
    }
    
    @Override
    @Transactional
    public void sendJoinMessage(ChatMessageRequest request) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
            .orElseThrow(() -> new ChatRoomNotFoundException());
        
        // 소모임 멤버 권한 확인
        validateGroupMembership(chatRoom.getGroup().getId(), request.getSenderId());
        
        // 입장 메시지 제거 (조용한 입장)
        // DB 저장이나 웹소켓 전송 없이 권한 확인만 수행
        
        log.info("조용한 입장 완료 - 채팅방: {}, 사용자: {}", 
            request.getChatRoomId(), request.getSenderName());
    }
    
    @Override
    @Transactional
    public void sendLeaveMessage(ChatMessageRequest request) {
        // 퇴장 메시지 전송 비활성화 (UX 개선: 페이지 전환 시 불필요한 알림 방지)
        log.info("퇴장 메시지 전송 스킵 - 채팅방: {}, 사용자: {}", 
            request.getChatRoomId(), request.getSenderName());
    }
    
    @Override
    @Transactional
    public void sendSystemMessage(Long chatRoomId, String content) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new ChatRoomNotFoundException());
        
        // DB에 시스템 메시지 저장
        ChatMessage systemMessage = new ChatMessage(
            chatRoom,
            content,
            MessageType.SYSTEM_NOTICE
        );
        messageRepository.save(systemMessage);
        
        // 웹소켓으로 실시간 전송
        ChatWebSocketMessage wsMessage = ChatWebSocketMessage.createSystemMessage(chatRoomId, content);
        
        messagingTemplate.convertAndSend(
            "/topic/chatroom/" + chatRoomId,
            wsMessage
        );
        
        log.info("시스템 메시지 전송 완료 - 채팅방: {}, 내용: {}", chatRoomId, content);
    }
    
    /**
     * 소모임 멤버인지 확인합니다. (지난 모임 포함, 탈퇴하지 않은 멤버만)
     */
    private void validateGroupMembership(Long groupId, Long userId) {
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
            .orElseThrow(() -> WebSocketException.accessDenied("소모임 멤버가 아닙니다"));
        
        // 소모임에서 탈퇴한 경우만 차단 (지난 모임은 허용)
        if (groupMember.getLeftAt() != null) {
            throw WebSocketException.accessDenied("탈퇴한 소모임 멤버입니다");
        }
    }
}