package com.helpie.backend.repository.chatroom;

import com.helpie.backend.domain.chatroom.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 채팅방 참여자 Repository
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {
    
    /**
     * 채팅방별 온라인 참여자 목록을 조회합니다.
     */
    List<ChatRoomParticipant> findByChatRoomIdAndIsOnline(Long chatRoomId, Boolean isOnline);
    
    /**
     * 사용자별 온라인 참여 채팅방 목록을 조회합니다.
     */
    List<ChatRoomParticipant> findByUserIdAndIsOnline(Long userId, Boolean isOnline);
    
    /**
     * 특정 채팅방의 특정 사용자 참여 정보를 조회합니다.
     */
    Optional<ChatRoomParticipant> findByChatRoomIdAndUserId(Long chatRoomId, Long userId);
    
    /**
     * 채팅방별 온라인 참여자 수를 조회합니다.
     */
    Long countByChatRoomIdAndIsOnline(Long chatRoomId, Boolean isOnline);
}