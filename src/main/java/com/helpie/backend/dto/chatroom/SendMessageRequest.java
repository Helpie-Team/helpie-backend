package com.helpie.backend.dto.chatroom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 메시지 전송 요청 DTO
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;
    
    @NotBlank(message = "사용자 이름은 필수입니다")
    private String userName;
    
    @NotBlank(message = "메시지 내용은 필수입니다")
    private String content;
}