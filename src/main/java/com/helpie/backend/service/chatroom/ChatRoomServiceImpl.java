package com.helpie.backend.service.chatroom;

import com.helpie.backend.domain.chatroom.ChatRoom;
import com.helpie.backend.domain.chatroom.ChatRoomParticipant;
import com.helpie.backend.domain.chatroom.ChatMessage;
import com.helpie.backend.domain.chatroom.MessageType;
import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.group.GroupMember;
import com.helpie.backend.domain.group.GroupStatus;
import com.helpie.backend.dto.chatroom.ChatRoomResponse;
import com.helpie.backend.dto.chatroom.ChatMessageResponse;
import com.helpie.backend.dto.chatroom.SendMessageRequest;
import com.helpie.backend.repository.chatroom.ChatRoomRepository;
import com.helpie.backend.repository.chatroom.ChatRoomParticipantRepository;
import com.helpie.backend.repository.chatroom.ChatMessageRepository;
import com.helpie.backend.repository.group.GroupMemberRepository;
import com.helpie.backend.exception.chatroom.ChatRoomNotFoundException;
import com.helpie.backend.exception.chatroom.ChatRoomAccessDeniedException;
import com.helpie.backend.exception.chatroom.ChatRoomNotJoinedException;
import com.helpie.backend.service.websocket.ChatWebSocketService;
import com.helpie.backend.service.user.UserImageService;
import com.helpie.backend.domain.user.UserImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 채팅방 서비스 구현체
 * 소모임 멤버만 접근 가능한 채팅방 관리를 담당합니다.
 * 
 * @author 전우선
 * @since 2025-10-30(토)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomServiceImpl implements ChatRoomService {
    
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository participantRepository;
    private final ChatMessageRepository messageRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ChatWebSocketService webSocketService;
    private final UserImageService userImageService;
    
    @Override
    @Transactional
    public ChatRoomResponse enterChatRoom(Long chatRoomId, Long userId, String userName) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new ChatRoomNotFoundException());
        
        // 소모임 멤버인지 확인
        validateGroupMembership(chatRoom.getGroup().getId(), userId);
        
        // 이미 입장해 있는지 확인
        Optional<ChatRoomParticipant> existingParticipant =
            participantRepository.findByChatRoomIdAndUserId(chatRoomId, userId);

//        if (existingParticipant.isPresent() && existingParticipant.get().getIsOnline()) {
//            log.info("사용자 {}가 이미 채팅방 {}에 입장해 있습니다", userId, chatRoomId);
//            return ChatRoomResponse.from(chatRoom);
//        }
        
        // 참여자 정보 저장 또는 업데이트
        if (existingParticipant.isPresent()) {
            existingParticipant.get().enter();
            participantRepository.save(existingParticipant.get());
        } else {
            ChatRoomParticipant participant = new ChatRoomParticipant(chatRoom, userId);
            participantRepository.save(participant);
        }
        
        // 채팅방 참여자 수 업데이트
        chatRoom.addParticipant(userId);
        chatRoomRepository.save(chatRoom);
        
        // 입장 시스템 메시지 제거 (소모임 가입 시만 환영 메시지 표시)
        
        log.info("사용자 {}가 채팅방 {}에 입장했습니다", userId, chatRoomId);
        return ChatRoomResponse.from(chatRoom);
    }
    
    @Override
    @Transactional
    public void leaveChatRoom(Long chatRoomId, Long userId, String userName) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new ChatRoomNotFoundException());
        
        ChatRoomParticipant participant = participantRepository
            .findByChatRoomIdAndUserId(chatRoomId, userId)
            .orElseThrow(() -> new ChatRoomNotJoinedException());
        
        if (!participant.getIsOnline()) {
            throw new ChatRoomNotJoinedException();
        }
        
        // 참여자 퇴장 처리
        participant.leave();
        participantRepository.save(participant);
        
        // 채팅방 참여자 수 감소
        chatRoom.removeParticipant(userId);
        chatRoomRepository.save(chatRoom);
        
        // 퇴장 시스템 메시지 전송 (웹소켓을 통해 실시간 전송)
        webSocketService.sendSystemMessage(chatRoomId, userName + "님이 채팅방을 나갔습니다.");
        
        log.info("사용자 {}가 채팅방 {}에서 퇴장했습니다", userId, chatRoomId);
    }
    
    @Override
    public List<ChatRoomResponse> getAccessibleChatRooms(Long userId) {
        List<ChatRoom> accessibleChatRooms = chatRoomRepository.findAccessibleChatRoomsByUserId(userId);
        
        return accessibleChatRooms.stream()
            .map(ChatRoomResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public ChatRoomResponse getChatRoomDetail(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new ChatRoomNotFoundException());
        
        // 소모임 멤버인지 확인
        validateGroupMembership(chatRoom.getGroup().getId(), userId);
        
        return ChatRoomResponse.from(chatRoom);
    }
    
    @Override
    public Page<ChatMessageResponse> getChatMessages(Long chatRoomId, Long userId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new ChatRoomNotFoundException());
        
        // 소모임 멤버인지 확인
        validateGroupMembership(chatRoom.getGroup().getId(), userId);
        
        return messageRepository
            .findByChatRoomIdAndIsDeletedFalseOrderBySentAtDesc(chatRoomId, pageable)
            .map(message -> {
                // 발신자 프로필 이미지 조회
                String profileImage = userImageService.getUserImage(message.getSenderId())
                    .map(UserImage::getImageUrl)
                    .orElse(null);
                return ChatMessageResponse.from(message, profileImage);
            });
    }
    
    @Override
    @Transactional
    public ChatMessageResponse sendMessage(Long chatRoomId, SendMessageRequest request) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new ChatRoomNotFoundException());
        
        // 소모임 멤버인지 확인
        validateGroupMembership(chatRoom.getGroup().getId(), request.getUserId());
        
        // 채팅방에 입장해 있는지 확인
        ChatRoomParticipant participant = participantRepository
            .findByChatRoomIdAndUserId(chatRoomId, request.getUserId())
            .orElseThrow(() -> new ChatRoomAccessDeniedException());
        
        if (!participant.getIsOnline()) {
            throw new ChatRoomAccessDeniedException();
        }
        
        // 메시지 저장
        ChatMessage message = new ChatMessage(
            chatRoom, 
            request.getUserId(), 
            request.getUserName(), 
            request.getContent()
        );
        ChatMessage savedMessage = messageRepository.save(message);
        
        // 발신자 프로필 이미지 조회
        String profileImage = userImageService.getUserImage(request.getUserId())
            .map(UserImage::getImageUrl)
            .orElse(null);
        
        log.info("사용자 {}가 채팅방 {}에 메시지를 전송했습니다", request.getUserId(), chatRoomId);
        return ChatMessageResponse.from(savedMessage, profileImage);
    }
    
    @Override
    @Transactional
    public Long createChatRoomAndJoin(Group group, Long userId, String userName, String welcomeMessage) {
        log.debug("소모임 채팅방 생성 및 자동 입장 - groupId: {}, userId: {}", group.getId(), userId);
        
        // 1. 채팅방 생성
        ChatRoom chatRoom = new ChatRoom(group);
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        log.info("채팅방 자동 생성 완료 - chatRoomId: {}, groupId: {}", savedChatRoom.getId(), group.getId());
        
        // 2. 사용자 자동 입장
        ChatRoomParticipant participant = new ChatRoomParticipant(savedChatRoom, userId);
        participantRepository.save(participant);
        log.info("채팅방 자동 입장 완료 - chatRoomId: {}, userId: {}", savedChatRoom.getId(), userId);
        
        // 3. 환영 메시지 전송
        if (welcomeMessage != null && !welcomeMessage.isEmpty()) {
            webSocketService.sendSystemMessage(savedChatRoom.getId(), welcomeMessage);
        }
        
        return savedChatRoom.getId();
    }
    
    @Override
    @Transactional
    public Long autoJoinGroupChatRoom(Long groupId, Long userId, String userName, String joinMessage) {
        log.debug("소모임 채팅방 자동 입장 - groupId: {}, userId: {}", groupId, userId);
        
        // 1. 채팅방 찾기
        ChatRoom chatRoom = chatRoomRepository.findByGroupId(groupId)
                .orElseThrow(() -> new IllegalStateException("채팅방이 존재하지 않습니다: " + groupId));
        
        // 2. 사용자 자동 입장
        ChatRoomParticipant participant = new ChatRoomParticipant(chatRoom, userId);
        participantRepository.save(participant);
        log.info("채팅방 자동 입장 완료 - chatRoomId: {}, userId: {}", chatRoom.getId(), userId);
        
        // 3. 가입 메시지 전송
        if (joinMessage != null && !joinMessage.isEmpty()) {
            webSocketService.sendSystemMessage(chatRoom.getId(), joinMessage);
        }

        return chatRoom.getId();
    }

    /**
     * 소모임 멤버인지 확인합니다.
     */
    private void validateGroupMembership(Long groupId, Long userId) {
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
            .orElseThrow(() -> new ChatRoomAccessDeniedException());
        
        if (!groupMember.getIsActive()) {
            throw new ChatRoomAccessDeniedException();
        }
    }
    
    @Override
    public boolean isChatEnabled(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + chatRoomId));
        
        Group group = chatRoom.getGroup();
        GroupStatus status = group.getStatus();
        
        // RECRUITING 또는 RECRUITMENT_CLOSED 상태일 때만 채팅 가능
        // COMPLETED 상태가 되면 채팅 차단
        return status == GroupStatus.RECRUITING || status == GroupStatus.RECRUITMENT_CLOSED;
    }
    
}