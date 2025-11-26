package com.helpie.backend.controller.chatroom;

import com.helpie.backend.domain.user.UserRole;
import com.helpie.backend.domain.user.UserVo;
import com.helpie.backend.dto.chatroom.ChatRoomResponse;
import com.helpie.backend.dto.chatroom.ChatMessageResponse;
import com.helpie.backend.dto.chatroom.SendMessageRequest;
import com.helpie.backend.service.chatroom.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채팅방 관리 컨트롤러
 * 소모임 내 채팅방 입장, 퇴장, 메시지 전송 기능을 제공합니다.
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Tag(name = "ChatRoom", description = "최적화된 채팅방 관리 API - 소모임 기반 실시간 채팅\n\n" +
        "**채팅 참여 조건:**\n" +
        "- 소모임 멤버만 채팅방 참여 가능\n" +
        "- 진행중/모집완료/지난 모임 모두 채팅 가능\n" +
        "- 지난 모임도 계속 채팅 소통 허용\n\n" +
        "**성능 최적화 기능:**\n" +
        "- 메시지 배치 처리 (최대 10개, 100ms 간격)\n" +
        "- GZIP 압축 (500바이트 이상 메시지)\n" +
        "- 비동기 처리 및 전용 스레드 풀\n" +
        "- JWT 기반 보안 인증\n\n" +
        "**실시간 기능은 WebSocket `/ws/chat` 사용**")
@RestController
@RequestMapping("/api/v1/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {
    
    private final ChatRoomService chatRoomService;
    
    @PostMapping("/{chatRoomId}/enter")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(
        summary = "채팅방 입장", 
        description = "소모임 멤버가 채팅방에 조용히 입장합니다.\n\n" +
                     "**주요 기능:**\n" +
                     "- 소모임 멤버 권한 확인\n" +
                     "- 진행중/모집완료/지난 모임 모두 입장 가능\n" +
                     "- 조용한 입장 (입장 알림 메시지 없음)\n" +
                     "- 참여자 수 업데이트\n\n" +
                     "**참고:**\n" +
                     "- 소모임 최초 가입 시에만 환영 메시지가 표시됩니다\n" +
                     "- 실제 실시간 채팅은 WebSocket `/ws/chat` 연결이 필요합니다"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "채팅방 입장 성공",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ChatRoomResponse.class))),
        @ApiResponse(responseCode = "403", description = "채팅방 접근 권한 없음"),
        @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음")
    })
    public ResponseEntity<ChatRoomResponse> enterChatRoom(
        @AuthenticationPrincipal UserVo userVo,
        @Parameter(description = "채팅방 ID") @PathVariable Long chatRoomId,
        @Parameter(description = "사용자 이름") @RequestParam String userName
    ) {
        ChatRoomResponse response = chatRoomService.enterChatRoom(chatRoomId, userVo.getId(), userName);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{chatRoomId}/leave")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "채팅방 퇴장", description = "채팅방에서 임시 퇴장합니다. 소모임 멤버 상태는 유지되며 언제든 재입장 가능합니다. 퇴장 시 '[사용자명]님이 채팅방을 나갔습니다.' 메시지가 전송됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "채팅방 퇴장 성공"),
        @ApiResponse(responseCode = "403", description = "채팅방 접근 권한 없음"),
        @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음")
    })
    public ResponseEntity<Void> leaveChatRoom(
        @AuthenticationPrincipal UserVo userVo,
        @Parameter(description = "채팅방 ID") @PathVariable Long chatRoomId,
        @Parameter(description = "사용자 이름") @RequestParam String userName
    ) {
        chatRoomService.leaveChatRoom(chatRoomId, userVo.getId(), userName);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/accessible")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(
        summary = "접근 가능한 채팅방 목록", 
        description = "사용자가 접근 가능한 채팅방 목록을 조회합니다.\n\n" +
                     "**조회 기준:**\n" +
                     "- 사용자가 소모임 멤버인 채팅방만 조회\n" +
                     "- **지난 모임(COMPLETED) 채팅방도 포함** - 지속적 소통 지원\n" +
                     "- 소모임에서 탈퇴한 멤버는 접근 불가\n" +
                     "- 현재 참여자 수는 소모임 실제 가입 멤버 수로 표시\n" +
                     "- 활성 상태인 채팅방만 포함\n\n" +
                     "**응답 정보:**\n" +
                     "- 채팅방 기본 정보 (ID, 제목, 참여자 수 등)\n" +
                     "- 소모임 정보 (제목, 대표 이미지, 지역, 카테고리)\n" +
                     "- 모바일 UI 구현에 필요한 모든 데이터 포함\n\n" +
                     "**중요:** currentParticipants는 이제 소모임 실제 가입 멤버 수를 정확히 표시합니다."
    )
    @ApiResponse(
        responseCode = "200", 
        description = "성공",
        content = @Content(
            schema = @Schema(implementation = ChatRoomResponse.class),
            examples = @ExampleObject(value = """
                [
                    {
                        "id": 1,
                        "groupId": 101,
                        "title": "일본 디즈니랜드 소모임 채팅방",
                        "currentParticipants": 4,
                        "totalMembers": 4,
                        "isActive": true,
                        "createdAt": "2025-11-20T10:00:00",
                        "groupTitle": "일본 디즈니랜드 소모임",
                        "groupThumbnail": "https://example.com/disney.jpg",
                        "location": "도쿄",
                        "category": "TRAVEL"
                    },
                    {
                        "id": 2,
                        "groupId": 102,
                        "title": "헬스 동호회 채팅방",
                        "currentParticipants": 8,
                        "totalMembers": 10,
                        "isActive": true,
                        "createdAt": "2025-11-18T15:30:00",
                        "groupTitle": "헬스 동호회",
                        "groupThumbnail": "https://example.com/fitness.jpg",
                        "location": "서울",
                        "category": "SPORTS"
                    },
                    {
                        "id": 3,
                        "groupId": 103,
                        "title": "[지난 모임] 제주도 여행 채팅방",
                        "currentParticipants": 6,
                        "totalMembers": 6,
                        "isActive": true,
                        "createdAt": "2025-11-15T12:00:00",
                        "groupTitle": "제주도 여행 소모임",
                        "groupThumbnail": "https://example.com/jeju.jpg",
                        "location": "제주",
                        "category": "TRAVEL"
                    }
                ]
                """)
        )
    )
    public ResponseEntity<List<ChatRoomResponse>> getAccessibleChatRooms(
        @AuthenticationPrincipal UserVo userVo
    ) {
        List<ChatRoomResponse> response = chatRoomService.getAccessibleChatRooms(userVo.getId());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{chatRoomId}")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "채팅방 상세 조회", description = "채팅방의 상세 정보를 조회합니다.")
    public ResponseEntity<ChatRoomResponse> getChatRoomDetail(
        @AuthenticationPrincipal UserVo userVo,
        @Parameter(description = "채팅방 ID") @PathVariable Long chatRoomId
    ) {
        ChatRoomResponse response = chatRoomService.getChatRoomDetail(chatRoomId, userVo.getId());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{chatRoomId}/messages")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(
        summary = "채팅방 메시지 조회 (페이징 최적화)",
        description = "채팅방의 메시지 목록을 페이징하여 조회합니다.\n\n" +
                     "**모바일 최적화 기능:**\n" +
                     "- 기본 50개씩 페이징 (무한 스크롤 지원)\n" +
                     "- 최신순 정렬 (sentAt DESC)\n" +
                     "- 삭제된 메시지 자동 제외\n" +
                     "- 소모임 멤버 권한 자동 검증\n\n" +
                     "**사용 예시:**\n" +
                     "- 최신 메시지: `?page=0&size=20`\n" +
                     "- 이전 메시지: `?page=1&size=20`"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "메시지 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "403", description = "채팅방 접근 권한 없음"),
        @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음")
    })
    public ResponseEntity<Page<ChatMessageResponse>> getChatMessages(
        @AuthenticationPrincipal UserVo userVo,
        @Parameter(description = "채팅방 ID") @PathVariable Long chatRoomId,
        @Parameter(description = "페이징 정보 (기본: 50개, 최신순)") 
        @PageableDefault(size = 50, sort = "sentAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ChatMessageResponse> response = chatRoomService.getChatMessages(chatRoomId, userVo.getId(), pageable);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{chatRoomId}/messages")
    @Operation(
        summary = "메시지 전송 (REST API)", 
        description = "REST API를 통해 채팅방에 메시지를 전송합니다.\n\n" +
                     "**참고:**\n" +
                     "- 이 API는 DB 저장용이며, 실시간 전송은 되지 않습니다\n" +
                     "- 실시간 채팅은 WebSocket `/app/chat/{chatRoomId}` 사용\n" +
                     "- 소모임 멤버이고 채팅방에 입장한 상태여야 합니다"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "메시지 전송 성공",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ChatMessageResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (메시지 내용 누락 등)"),
        @ApiResponse(responseCode = "403", description = "채팅방 접근 권한 없음"),
        @ApiResponse(responseCode = "404", description = "채팅방을 찾을 수 없음")
    })
    public ResponseEntity<ChatMessageResponse> sendMessage(
        @Parameter(description = "채팅방 ID") @PathVariable Long chatRoomId,
        @Parameter(description = "메시지 전송 요청 (content, userId, userName 포함)")
        @Valid @RequestBody SendMessageRequest request
    ) {
        ChatMessageResponse response = chatRoomService.sendMessage(chatRoomId, request);
        return ResponseEntity.ok(response);
    }
}