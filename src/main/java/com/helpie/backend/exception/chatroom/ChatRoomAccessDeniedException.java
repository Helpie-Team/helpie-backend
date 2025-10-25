package com.helpie.backend.exception.chatroom;

import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.ErrorCode;

/**
 * 채팅방 접근 권한 없음 예외
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
public class ChatRoomAccessDeniedException extends BusinessException {
    
    public ChatRoomAccessDeniedException(Long chatRoomId, Long userId) {
        super(ErrorCode.CHATROOM_ACCESS_DENIED,
              "사용자 ID " + userId + "는 채팅방 ID " + chatRoomId + "에 접근할 수 없습니다.");
    }
    
    public ChatRoomAccessDeniedException() {
        super(ErrorCode.CHATROOM_ACCESS_DENIED);
    }
}