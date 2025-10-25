package com.helpie.backend.repository.chatroom;

import com.helpie.backend.domain.chatroom.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 채팅방 Repository
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    /**
     * 소모임별 채팅방을 조회합니다.
     */
    Optional<ChatRoom> findByGroupId(Long groupId);
    
    /**
     * 활성 채팅방 목록을 조회합니다.
     */
    List<ChatRoom> findByIsActive(Boolean isActive);
    
    /**
     * 사용자가 참여 가능한 채팅방 목록을 조회합니다. (소모임 멤버인 채팅방)
     */
    @Query("SELECT cr FROM ChatRoom cr " +
           "JOIN cr.group g " +
           "JOIN g.members gm " +
           "WHERE gm.userId = :userId AND gm.isActive = true AND cr.isActive = true")
    List<ChatRoom> findAccessibleChatRoomsByUserId(@Param("userId") Long userId);
}