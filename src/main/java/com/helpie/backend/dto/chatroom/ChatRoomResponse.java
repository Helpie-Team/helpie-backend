package com.helpie.backend.dto.chatroom;

import com.helpie.backend.domain.chatroom.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 채팅방 응답 DTO
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {
    
    private Long id;
    private Long groupId;
    private String title;
    private Integer currentParticipants;
    private Integer totalMembers; // 소모임 총 멤버 수
    private Boolean isActive;
    private LocalDateTime createdAt;
    
    public static ChatRoomResponse from(ChatRoom chatRoom) {
        return new ChatRoomResponse(
            chatRoom.getId(),
            chatRoom.getGroup().getId(),
            chatRoom.getTitle(),
            chatRoom.getCurrentParticipants(),
            chatRoom.getGroup().getCurrentMembers(),
            chatRoom.getIsActive(),
            chatRoom.getCreatedAt()
        );
    }
}