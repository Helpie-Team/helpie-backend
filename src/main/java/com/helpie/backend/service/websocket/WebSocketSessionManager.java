package com.helpie.backend.service.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 웹소켓 세션 관리자
 * 사용자의 온라인 상태 및 세션 정보를 관리합니다.
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Slf4j
@Component
public class WebSocketSessionManager {
    
    // 세션 ID -> 사용자 정보 매핑
    private final ConcurrentMap<String, UserSession> sessionMap = new ConcurrentHashMap<>();
    
    // 사용자 ID -> 세션 ID 매핑 (중복 연결 방지용)
    private final ConcurrentMap<Long, String> userSessionMap = new ConcurrentHashMap<>();
    
    // 채팅방 ID -> 온라인 사용자 ID 세트 매핑
    private final ConcurrentMap<Long, Set<Long>> chatRoomUsersMap = new ConcurrentHashMap<>();
    
    /**
     * 사용자 세션 등록
     */
    public void registerSession(String sessionId, Long userId, String userName, Long chatRoomId) {
        // 기존 세션이 있다면 정리
        String existingSessionId = userSessionMap.get(userId);
        if (existingSessionId != null && !existingSessionId.equals(sessionId)) {
            log.info("사용자 {}의 기존 세션 {} 정리 중", userId, existingSessionId);
            removeSession(existingSessionId);
        }
        
        UserSession userSession = new UserSession(sessionId, userId, userName, chatRoomId);
        sessionMap.put(sessionId, userSession);
        userSessionMap.put(userId, sessionId);
        
        // 채팅방에 사용자 추가
        if (chatRoomId != null) {
            chatRoomUsersMap.computeIfAbsent(chatRoomId, k -> ConcurrentHashMap.newKeySet()).add(userId);
        }
        
        log.info("세션 등록 완료 - 세션: {}, 사용자: {}, 채팅방: {}", sessionId, userId, chatRoomId);
    }
    
    /**
     * 세션 제거
     */
    public UserSession removeSession(String sessionId) {
        UserSession session = sessionMap.remove(sessionId);
        if (session != null) {
            userSessionMap.remove(session.getUserId());
            
            // 채팅방에서 사용자 제거
            if (session.getChatRoomId() != null) {
                Set<Long> roomUsers = chatRoomUsersMap.get(session.getChatRoomId());
                if (roomUsers != null) {
                    roomUsers.remove(session.getUserId());
                    if (roomUsers.isEmpty()) {
                        chatRoomUsersMap.remove(session.getChatRoomId());
                    }
                }
            }
            
            log.info("세션 제거 완료 - 세션: {}, 사용자: {}", sessionId, session.getUserId());
        }
        return session;
    }
    
    /**
     * 세션 정보 조회
     */
    public UserSession getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }
    
    /**
     * 사용자의 세션 정보 조회
     */
    public UserSession getUserSession(Long userId) {
        String sessionId = userSessionMap.get(userId);
        return sessionId != null ? sessionMap.get(sessionId) : null;
    }
    
    /**
     * 사용자가 온라인 상태인지 확인
     */
    public boolean isUserOnline(Long userId) {
        return userSessionMap.containsKey(userId);
    }
    
    /**
     * 채팅방의 온라인 사용자 목록 조회
     */
    public Set<Long> getOnlineUsersInChatRoom(Long chatRoomId) {
        return chatRoomUsersMap.getOrDefault(chatRoomId, Set.of());
    }
    
    /**
     * 채팅방의 온라인 사용자 수 조회
     */
    public int getOnlineUserCountInChatRoom(Long chatRoomId) {
        Set<Long> users = chatRoomUsersMap.get(chatRoomId);
        return users != null ? users.size() : 0;
    }
    
    /**
     * 전체 활성 세션 수 조회
     */
    public int getActiveSessionCount() {
        return sessionMap.size();
    }
    
    /**
     * 전체 온라인 사용자 수 조회
     */
    public int getOnlineUserCount() {
        return userSessionMap.size();
    }
    
    
    /**
     * 사용자의 채팅방 변경
     */
    public void updateUserChatRoom(String sessionId, Long newChatRoomId) {
        UserSession session = sessionMap.get(sessionId);
        if (session != null) {
            Long oldChatRoomId = session.getChatRoomId();
            
            // 이전 채팅방에서 제거
            if (oldChatRoomId != null) {
                Set<Long> oldRoomUsers = chatRoomUsersMap.get(oldChatRoomId);
                if (oldRoomUsers != null) {
                    oldRoomUsers.remove(session.getUserId());
                    if (oldRoomUsers.isEmpty()) {
                        chatRoomUsersMap.remove(oldChatRoomId);
                    }
                }
            }
            
            // 새 채팅방에 추가
            if (newChatRoomId != null) {
                chatRoomUsersMap.computeIfAbsent(newChatRoomId, k -> ConcurrentHashMap.newKeySet())
                               .add(session.getUserId());
            }
            
            session.setChatRoomId(newChatRoomId);
            session.setLastActivity(LocalDateTime.now());
            
            log.info("사용자 {}의 채팅방 변경: {} -> {}", session.getUserId(), oldChatRoomId, newChatRoomId);
        }
    }
    
    /**
     * 세션 활동 시간 업데이트
     */
    public void updateLastActivity(String sessionId) {
        UserSession session = sessionMap.get(sessionId);
        if (session != null) {
            session.setLastActivity(LocalDateTime.now());
        }
    }
    
    /**
     * 비활성 세션 정리 (일정 시간 이상 비활성 세션 제거)
     */
    public void cleanupInactiveSessions(int inactiveMinutes) {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(inactiveMinutes);
        
        Set<String> inactiveSessions = sessionMap.entrySet().stream()
            .filter(entry -> entry.getValue().getLastActivity().isBefore(cutoff))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
        
        inactiveSessions.forEach(this::removeSession);
        
        if (!inactiveSessions.isEmpty()) {
            log.info("비활성 세션 {} 개 정리 완료", inactiveSessions.size());
        }
    }
    
    /**
     * 사용자 세션 정보 클래스
     */
    public static class UserSession {
        private final String sessionId;
        private final Long userId;
        private final String userName;
        private Long chatRoomId;
        private LocalDateTime lastActivity;
        private final LocalDateTime connectedAt;
        
        public UserSession(String sessionId, Long userId, String userName, Long chatRoomId) {
            this.sessionId = sessionId;
            this.userId = userId;
            this.userName = userName;
            this.chatRoomId = chatRoomId;
            this.lastActivity = LocalDateTime.now();
            this.connectedAt = LocalDateTime.now();
        }
        
        // Getters and Setters
        public String getSessionId() { return sessionId; }
        public Long getUserId() { return userId; }
        public String getUserName() { return userName; }
        public Long getChatRoomId() { return chatRoomId; }
        public void setChatRoomId(Long chatRoomId) { this.chatRoomId = chatRoomId; }
        public LocalDateTime getLastActivity() { return lastActivity; }
        public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }
        public LocalDateTime getConnectedAt() { return connectedAt; }
    }
}