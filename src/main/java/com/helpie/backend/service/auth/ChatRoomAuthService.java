package com.helpie.backend.service.auth;

import com.helpie.backend.domain.chatroom.ChatRoom;
import com.helpie.backend.domain.group.GroupMember;
import com.helpie.backend.repository.chatroom.ChatRoomRepository;
import com.helpie.backend.repository.group.GroupMemberRepository;
import com.helpie.backend.exception.chatroom.ChatRoomNotFoundException;
import com.helpie.backend.exception.chatroom.ChatRoomAccessDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 채팅방 접근 권한 검증 서비스
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomAuthService {
    
    private final ChatRoomRepository chatRoomRepository;
    private final GroupMemberRepository groupMemberRepository;
    
    /**
     * 사용자가 채팅방에 접근할 수 있는지 확인
     * 
     * @param chatRoomId 채팅방 ID
     * @param userId 사용자 ID
     * @throws ChatRoomNotFoundException 채팅방이 존재하지 않는 경우
     * @throws ChatRoomAccessDeniedException 접근 권한이 없는 경우
     */
    public void validateChatRoomAccess(Long chatRoomId, Long userId) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new ChatRoomNotFoundException(chatRoomId));
        
        // 소모임 멤버인지 확인
        Long groupId = chatRoom.getGroup().getId();
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
            .orElseThrow(() -> new ChatRoomAccessDeniedException(chatRoomId, userId));
        
        // 활성 멤버인지 확인
        if (!groupMember.getIsActive()) {
            log.warn("비활성 소모임 멤버가 채팅방 접근 시도 - 채팅방: {}, 사용자: {}", chatRoomId, userId);
            throw new ChatRoomAccessDeniedException(chatRoomId, userId);
        }
        
        log.debug("채팅방 접근 권한 확인 완료 - 채팅방: {}, 사용자: {}", chatRoomId, userId);
    }
    
    /**
     * 사용자가 특정 소모임의 멤버인지 확인
     * 
     * @param groupId 소모임 ID
     * @param userId 사용자 ID
     * @return 멤버 여부
     */
    public boolean isGroupMember(Long groupId, Long userId) {
        return groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
            .map(GroupMember::getIsActive)
            .orElse(false);
    }
    
    /**
     * 사용자가 채팅방에 접근할 수 있는지 확인 (예외 발생 없이)
     * 
     * @param chatRoomId 채팅방 ID
     * @param userId 사용자 ID
     * @return 접근 가능 여부
     */
    public boolean canAccessChatRoom(Long chatRoomId, Long userId) {
        try {
            validateChatRoomAccess(chatRoomId, userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}