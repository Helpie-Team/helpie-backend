package com.helpie.backend.exception.chatroom;

import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.ErrorCode;

/**
 * 채팅방 조회 실패 예외
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
public class ChatRoomNotFoundException extends BusinessException {
    
    public ChatRoomNotFoundException(Long chatRoomId) {
        super(ErrorCode.CHATROOM_NOT_FOUND,
              "채팅방 ID " + chatRoomId + "를 찾을 수 없습니다.");
    }
    
    public ChatRoomNotFoundException() {
        super(ErrorCode.CHATROOM_NOT_FOUND);
    }
}