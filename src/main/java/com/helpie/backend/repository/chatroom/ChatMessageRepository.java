package com.helpie.backend.repository.chatroom;

import com.helpie.backend.domain.chatroom.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 채팅 메시지 Repository
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    /**
     * 채팅방별 메시지 목록을 페이징하여 조회합니다.
     */
    Page<ChatMessage> findByChatRoomIdAndIsDeletedFalseOrderBySentAtDesc(
        Long chatRoomId, 
        Pageable pageable
    );
    
    /**
     * 채팅방별 최근 메시지 목록을 조회합니다.
     */
    List<ChatMessage> findTop50ByChatRoomIdAndIsDeletedFalseOrderBySentAtDesc(Long chatRoomId);
    
    /**
     * 특정 시간 이후의 메시지 목록을 조회합니다.
     */
    List<ChatMessage> findByChatRoomIdAndSentAtAfterAndIsDeletedFalseOrderBySentAt(
        Long chatRoomId, 
        LocalDateTime after
    );
}