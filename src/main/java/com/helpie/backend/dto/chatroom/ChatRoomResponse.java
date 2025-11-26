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
    
    @Schema(description = "소모임 현재 참여자 수", example = "23")
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
    
    @Schema(description = "마지막 메시지 내용", example = "안녕하세요! 내일 모임 시간 확인드려요")
    private String lastMessage;
    
    @Schema(description = "마지막 메시지 발송 시간", example = "2025-11-26T15:30:00")
    private LocalDateTime lastMessageTime;
    
    @Schema(description = "마지막 메시지 발신자명", example = "홍길동")
    private String lastMessageSender;
    
    public static ChatRoomResponse from(ChatRoom chatRoom) {
        return new ChatRoomResponse(
            chatRoom.getId(),
            chatRoom.getGroup().getId(),
            chatRoom.getTitle(),
            chatRoom.getGroup().getCurrentMembers(), // 소모임 가입 멤버 수 사용
            chatRoom.getGroup().getCurrentMembers(),
            chatRoom.getIsActive(),
            chatRoom.getCreatedAt(),
            chatRoom.getGroup().getTitle(),
            chatRoom.getGroup().getThumbnail(),
            chatRoom.getGroup().getCity().getName(),
            chatRoom.getGroup().getCategory().name(),
            null, // lastMessage
            null, // lastMessageTime 
            null  // lastMessageSender
        );
    }
    
    public static ChatRoomResponse fromWithLastMessage(ChatRoom chatRoom, String lastMessage, 
                                                      LocalDateTime lastMessageTime, String lastMessageSender) {
        return new ChatRoomResponse(
            chatRoom.getId(),
            chatRoom.getGroup().getId(),
            chatRoom.getTitle(),
            chatRoom.getGroup().getCurrentMembers(),
            chatRoom.getGroup().getCurrentMembers(),
            chatRoom.getIsActive(),
            chatRoom.getCreatedAt(),
            chatRoom.getGroup().getTitle(),
            chatRoom.getGroup().getThumbnail(),
            chatRoom.getGroup().getCity().getName(),
            chatRoom.getGroup().getCategory().name(),
            lastMessage,
            lastMessageTime,
            lastMessageSender
        );
    }
}