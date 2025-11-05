package com.helpie.backend.service.chatroom;

import com.helpie.backend.domain.group.Group;
import com.helpie.backend.dto.chatroom.ChatRoomResponse;
import com.helpie.backend.dto.chatroom.ChatMessageResponse;
import com.helpie.backend.dto.chatroom.SendMessageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 채팅방 서비스 인터페이스
 * 
 * @author 전우선
 * @since 2025-10-30(목)
 */
public interface ChatRoomService {
    
    /**
     * 채팅방에 입장합니다.
     * 
     * @param chatRoomId 채팅방 ID
     * @param userId 사용자 ID
     * @param userName 사용자 이름
     * @return 채팅방 정보
     */
    ChatRoomResponse enterChatRoom(Long chatRoomId, Long userId, String userName);
    
    /**
     * 채팅방이 활성화 상태인지 확인합니다.
     * 소모임 상태가 RECRUITING 또는 RECRUITMENT_CLOSED일 때만 채팅 가능
     * 
     * @param chatRoomId 채팅방 ID
     * @return 채팅 가능 여부
     */
    boolean isChatEnabled(Long chatRoomId);
    
    /**
     * 채팅방에서 퇴장합니다. (자동 퇴장)
     * 
     * @param chatRoomId 채팅방 ID
     * @param userId 사용자 ID
     * @param userName 사용자 이름
     */
    void leaveChatRoom(Long chatRoomId, Long userId, String userName);
    
    /**
     * 사용자가 접근 가능한 채팅방 목록을 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 접근 가능한 채팅방 목록
     */
    List<ChatRoomResponse> getAccessibleChatRooms(Long userId);
    
    /**
     * 채팅방 상세 정보를 조회합니다.
     * 
     * @param chatRoomId 채팅방 ID
     * @param userId 사용자 ID (접근 권한 확인용)
     * @return 채팅방 상세 정보
     */
    ChatRoomResponse getChatRoomDetail(Long chatRoomId, Long userId);
    
    /**
     * 채팅방의 메시지 목록을 조회합니다.
     * 
     * @param chatRoomId 채팅방 ID
     * @param userId 사용자 ID (접근 권한 확인용)
     * @param pageable 페이징 정보
     * @return 메시지 목록
     */
    Page<ChatMessageResponse> getChatMessages(Long chatRoomId, Long userId, Pageable pageable);
    
    /**
     * 메시지를 전송합니다.
     * 
     * @param chatRoomId 채팅방 ID
     * @param request 메시지 전송 요청
     * @return 전송된 메시지 정보
     */
    ChatMessageResponse sendMessage(Long chatRoomId, SendMessageRequest request);
    
    /**
     * 소모임에 대한 채팅방을 생성하고 사용자를 자동 입장시킵니다.
     * 
     * @param group 소모임
     * @param userId 사용자 ID
     * @param userName 사용자 이름
     * @param welcomeMessage 환영 메시지
     * @return 생성된 채팅방 ID
     */
    Long createChatRoomAndJoin(Group group, Long userId, String userName, String welcomeMessage);
    
    /**
     * 사용자를 소모임 채팅방에 자동 입장시킵니다.
     * 
     * @param groupId 소모임 ID
     * @param userId 사용자 ID
     * @param userName 사용자 이름
     * @param joinMessage 가입 메시지
     */
    void autoJoinGroupChatRoom(Long groupId, Long userId, String userName, String joinMessage);
}