package com.helpie.backend.repository.chatroom;

import com.helpie.backend.domain.chatroom.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    
    /**
     * 채팅방별 마지막 메시지를 조회합니다.
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id = :chatRoomId AND m.isDeleted = false ORDER BY m.sentAt DESC")
    List<ChatMessage> findFirstByChatRoomIdAndIsDeletedFalseOrderBySentAtDesc(@Param("chatRoomId") Long chatRoomId);
    
    /**
     * 여러 채팅방의 마지막 메시지를 일괄 조회합니다.
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id IN :chatRoomIds AND m.isDeleted = false " +
           "AND m.sentAt = (SELECT MAX(m2.sentAt) FROM ChatMessage m2 WHERE m2.chatRoom.id = m.chatRoom.id AND m2.isDeleted = false)")
    List<ChatMessage> findLastMessagesByChatRoomIds(@Param("chatRoomIds") List<Long> chatRoomIds);
}