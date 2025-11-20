package com.helpie.backend.dto.chatroom;

import com.helpie.backend.domain.chatroom.ChatRoom;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "채팅방 정보 응답")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {
    
    @Schema(description = "채팅방 ID", example = "24")
    private Long id;
    
    @Schema(description = "소모임 ID", example = "110")
    private Long groupId;
    
    @Schema(description = "채팅방 제목", example = "5555555 채팅방")
    private String title;
    
    @Schema(description = "현재 채팅방 참여자 수", example = "23")
    private Integer currentParticipants;
    
    @Schema(description = "소모임 총 멤버 수", example = "3")
    private Integer totalMembers;
    
    @Schema(description = "채팅방 활성 상태", example = "true")
    private Boolean isActive;
    
    @Schema(description = "채팅방 생성일시", example = "2025-11-16T14:30:28.049877")
    private LocalDateTime createdAt;
    
    @Schema(description = "소모임 제목", example = "중랑구 국밥투어 갈이해요~")
    private String groupTitle;
    
    @Schema(description = "소모임 대표 이미지 URL", example = "https://example.com/image.jpg")
    private String profileImageUrl;
    
    @Schema(description = "소모임 지역", example = "서울")
    private String location;
    
    @Schema(description = "소모임 카테고리", example = "ACTIVITY_LIFE")
    private String category;
    
    public static ChatRoomResponse from(ChatRoom chatRoom) {
        return new ChatRoomResponse(
            chatRoom.getId(),
            chatRoom.getGroup().getId(),
            chatRoom.getTitle(),
            chatRoom.getCurrentParticipants(),
            chatRoom.getGroup().getCurrentMembers(),
            chatRoom.getIsActive(),
            chatRoom.getCreatedAt(),
            chatRoom.getGroup().getTitle(),
            chatRoom.getGroup().getThumbnail(),
            chatRoom.getGroup().getCity().getName(),
            chatRoom.getGroup().getCategory().name()
        );
    }
}