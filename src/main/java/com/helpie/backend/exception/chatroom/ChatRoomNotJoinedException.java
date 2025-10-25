package com.helpie.backend.exception.chatroom;

import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.ErrorCode;

/**
 * 채팅방 미가입 예외
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
public class ChatRoomNotJoinedException extends BusinessException {
    
    public ChatRoomNotJoinedException(Long chatRoomId, Long userId) {
        super(ErrorCode.CHATROOM_NOT_JOINED,
              "사용자 ID " + userId + "는 채팅방 ID " + chatRoomId + "에 가입하지 않았습니다.");
    }
    
    public ChatRoomNotJoinedException() {
        super(ErrorCode.CHATROOM_NOT_JOINED);
    }
}