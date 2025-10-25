package com.helpie.backend.config;

import com.helpie.backend.domain.user.UserJwtClaim;
import com.helpie.backend.service.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * WebSocket 연결 시 JWT 토큰 인증을 처리하는 인터셉터
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    
    private final JwtTokenProvider jwtTokenProvider;
    
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // CONNECT 명령어 시 인증 처리
            authenticateUser(accessor);
        }
        
        return message;
    }
    
    /**
     * 사용자 인증 처리
     */
    private void authenticateUser(StompHeaderAccessor accessor) {
        try {
            // Authorization 헤더에서 토큰 추출
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("WebSocket 연결 시 Authorization 헤더가 없거나 형식이 올바르지 않습니다.");
                return;
            }
            
            String token = authHeader.substring(7);
            
            // 토큰 유효성 검증
            if (!jwtTokenProvider.validateToken(token)) {
                log.warn("WebSocket 연결 시 유효하지 않은 JWT 토큰입니다.");
                return;
            }
            
            // 토큰에서 사용자 정보 추출
            UserJwtClaim userClaim = jwtTokenProvider.decodeJwt(token, UserJwtClaim.class);
            Long userId = userClaim.getId();
            String username = userClaim.getUsername();
            
            // 세션에 사용자 정보 저장
            accessor.getSessionAttributes().put("authenticated", true);
            accessor.getSessionAttributes().put("userId", userId);
            accessor.getSessionAttributes().put("userName", username);
            
            // Security Context에 인증 정보 설정
            Authentication auth = new UsernamePasswordAuthenticationToken(
                userId, 
                null, 
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            accessor.setUser(auth);
            
            log.info("WebSocket 연결 인증 성공 - 사용자 ID: {}, 이름: {}", userId, username);
            
        } catch (Exception e) {
            log.error("WebSocket 인증 처리 중 오류 발생", e);
            // 인증 실패 시 연결 허용하지만 인증되지 않은 상태로 표시
            accessor.getSessionAttributes().put("authenticated", false);
        }
    }
}