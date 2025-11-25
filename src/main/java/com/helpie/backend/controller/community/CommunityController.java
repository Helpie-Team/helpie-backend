package com.helpie.backend.controller.community;

import com.helpie.backend.domain.community.CommunityCategory;
import com.helpie.backend.domain.user.UserRole;
import com.helpie.backend.domain.user.UserVo;
import com.helpie.backend.dto.community.CommunityCreateRequest;
import com.helpie.backend.dto.community.CommunityResponse;
import com.helpie.backend.dto.community.CommunityUpdateRequest;
import com.helpie.backend.dto.community.CommunityCommentRequest;
import com.helpie.backend.dto.community.CommunityCommentResponse;
import com.helpie.backend.service.community.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 커뮤니티 컨트롤러
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Tag(name = "Community", description = "커뮤니티 게시판 API\n\n" +
        "**주요 기능:**\n" +
        "- 정보공유, 자유게시판 카테고리별 게시글 관리\n" +
        "- 이미지 첨부 지원 (최대 4개)\n" +
        "- 제목/내용 검색 기능\n" +
        "- 전체 조회 및 카테고리별 조회")
@RestController
@RequestMapping("/api/v1/communities")
@RequiredArgsConstructor
public class CommunityController {
    
    private final CommunityService communityService;
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(
        summary = "커뮤니티 게시글 작성",
        description = "새로운 커뮤니티 게시글을 작성합니다.\n\n" +
                     "**요청 형식:**\n" +
                     "- Content-Type: multipart/form-data\n" +
                     "- category: 카테고리 (INFO_SHARE, FREE_BOARD)\n" +
                     "- title: 제목 (필수, 1-200자)\n" +
                     "- content: 내용 (필수, 1-10000자)\n" +
                     "- images: 첨부 이미지 (선택, 최대 4개)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게시글 작성 성공",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CommunityResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (필수 필드 누락, 유효성 검사 실패)"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    public ResponseEntity<CommunityResponse> createCommunity(
        @AuthenticationPrincipal UserVo userVo,
        @Parameter(description = "카테고리") @RequestParam CommunityCategory category,
        @Parameter(description = "제목") @RequestParam String title,
        @Parameter(description = "내용") @RequestParam String content,
        @Parameter(description = "첨부 이미지 (최대 4개)") @RequestParam(required = false) List<MultipartFile> images
    ) {
        CommunityCreateRequest request = new CommunityCreateRequest(category, title, content);
        CommunityResponse response = communityService.createCommunity(
            userVo.getId(), 
            userVo.getUsername(), 
            request, 
            images
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{communityId}")
    @Operation(summary = "커뮤니티 게시글 상세 조회", description = "게시글 ID로 상세 내용을 조회합니다. 조회 시 조회수가 1 증가합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글")
    })
    public ResponseEntity<CommunityResponse> getCommunityDetail(
        @Parameter(description = "게시글 ID") @PathVariable Long communityId
    ) {
        CommunityResponse response = communityService.getCommunityDetail(communityId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(
        summary = "커뮤니티 게시글 목록 조회",
        description = "전체 또는 카테고리별 게시글 목록을 조회합니다.\n\n" +
                     "**사용 예시:**\n" +
                     "- 전체: `/api/v1/communities`\n" +
                     "- 정보공유: `/api/v1/communities?category=INFO_SHARE`\n" +
                     "- 자유게시판: `/api/v1/communities?category=FREE_BOARD`"
    )
    public ResponseEntity<Page<CommunityResponse>> getCommunities(
        @Parameter(description = "카테고리 (선택)") @RequestParam(required = false) CommunityCategory category,
        @Parameter(
            description = "페이징 정보 (기본: 10개, 최신순)",
            example = """
                {
                  "page": 0,
                  "size": 10,
                  "sort": ["createdAt,desc"]
                }
                """
        )
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CommunityResponse> response = category != null 
            ? communityService.getCommunitiesByCategory(category, pageable)
            : communityService.getCommunities(pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "내 게시글 조회", description = "로그인한 사용자가 작성한 게시글 목록을 조회합니다.")
    public ResponseEntity<Page<CommunityResponse>> getMyCommunities(
        @AuthenticationPrincipal UserVo userVo,
        @Parameter(
            description = "페이징 정보 (기본: 10개, 최신순)",
            example = """
                {
                  "page": 0,
                  "size": 10,
                  "sort": ["createdAt,desc"]
                }
                """
        )
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CommunityResponse> response = communityService.getMyCommunities(userVo.getId(), pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    @Operation(
        summary = "게시글 검색",
        description = "제목 또는 내용으로 게시글을 검색합니다.\n\n" +
                     "**사용 예시:**\n" +
                     "- 전체 검색: `/api/v1/communities/search?keyword=검색어`\n" +
                     "- 카테고리별 검색: `/api/v1/communities/search?category=INFO_SHARE&keyword=검색어`"
    )
    public ResponseEntity<Page<CommunityResponse>> searchCommunities(
        @Parameter(description = "검색 키워드") @RequestParam String keyword,
        @Parameter(description = "카테고리 (선택)") @RequestParam(required = false) CommunityCategory category,
        @Parameter(
            description = "페이징 정보 (기본: 10개, 최신순)",
            example = """
                {
                  "page": 0,
                  "size": 10,
                  "sort": ["createdAt,desc"]
                }
                """
        )
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CommunityResponse> response = category != null
            ? communityService.searchCommunitiesByCategory(category, keyword, pageable)
            : communityService.searchCommunities(keyword, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/popular")
    @Operation(
        summary = "실시간 인기글 조회", 
        description = "조회수 기준 상위 5개 인기 게시글을 조회합니다.\n\n" +
                     "**사용 목적:**\n" +
                     "- 우측 사이드바 '실시간 인기글' 섹션에 표시\n" +
                     "- 조회수가 높은 게시글 순으로 정렬"
    )
    public ResponseEntity<List<CommunityResponse>> getPopularCommunities() {
        List<CommunityResponse> response = communityService.getPopularCommunities();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/recommended")
    @Operation(
        summary = "추천글 조회", 
        description = "좋아요 수 기준 상위 5개 추천 게시글을 조회합니다.\n\n" +
                     "**사용 목적:**\n" +
                     "- 우측 사이드바 '추천글' 섹션에 표시\n" +
                     "- 좋아요 수가 많은 게시글 순으로 정렬"
    )
    public ResponseEntity<List<CommunityResponse>> getRecommendedCommunities() {
        List<CommunityResponse> response = communityService.getRecommendedCommunities();
        return ResponseEntity.ok(response);
    }
    
    @PutMapping(value = "/{communityId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "커뮤니티 게시글 수정", description = "작성자만 자신의 게시글을 수정할 수 있습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
        @ApiResponse(responseCode = "403", description = "수정 권한 없음 (작성자가 아님)"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글")
    })
    public ResponseEntity<CommunityResponse> updateCommunity(
        @AuthenticationPrincipal UserVo userVo,
        @Parameter(description = "게시글 ID") @PathVariable Long communityId,
        @Parameter(description = "카테고리") @RequestParam CommunityCategory category,
        @Parameter(description = "제목") @RequestParam String title,
        @Parameter(description = "내용") @RequestParam String content,
        @Parameter(description = "첨부 이미지 (최대 4개)") @RequestParam(required = false) List<MultipartFile> images
    ) {
        CommunityUpdateRequest request = new CommunityUpdateRequest(category, title, content);
        CommunityResponse response = communityService.updateCommunity(
            communityId,
            userVo.getId(),
            request,
            images
        );
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{communityId}")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "커뮤니티 게시글 삭제", description = "작성자만 자신의 게시글을 삭제할 수 있습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
        @ApiResponse(responseCode = "403", description = "삭제 권한 없음 (작성자가 아님)"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글")
    })
    public ResponseEntity<Void> deleteCommunity(
        @AuthenticationPrincipal UserVo userVo,
        @Parameter(description = "게시글 ID") @PathVariable Long communityId
    ) {
        communityService.deleteCommunity(communityId, userVo.getId());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{communityId}/comments")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "커뮤니티 댓글 작성", description = "커뮤니티 게시글에 댓글을 작성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "댓글 작성 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글")
    })
    public ResponseEntity<CommunityCommentResponse> createComment(
        @AuthenticationPrincipal UserVo userVo,
        @Parameter(description = "게시글 ID") @PathVariable Long communityId,
        @Valid @RequestBody CommunityCommentRequest request
    ) {
        CommunityCommentResponse response = communityService.createComment(
            communityId, userVo.getId(), userVo.getUsername(), request
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{communityId}/comments")
    @Operation(summary = "커뮤니티 댓글 목록 조회", description = "커뮤니티 게시글의 댓글 목록을 조회합니다.")
    public ResponseEntity<Page<CommunityCommentResponse>> getComments(
        @Parameter(description = "게시글 ID") @PathVariable Long communityId,
        @Parameter(
            description = "페이징 정보 (기본: 20개, 작성일순)",
            example = """
                {
                  "page": 0,
                  "size": 20,
                  "sort": ["createdAt,asc"]
                }
                """
        )
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<CommunityCommentResponse> response = communityService.getComments(communityId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/comments/{commentId}")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "커뮤니티 댓글 삭제", description = "작성자만 자신의 댓글을 삭제할 수 있습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
        @ApiResponse(responseCode = "403", description = "삭제 권한 없음 (작성자가 아님)"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 댓글")
    })
    public ResponseEntity<Void> deleteComment(
        @AuthenticationPrincipal UserVo userVo,
        @Parameter(description = "댓글 ID") @PathVariable Long commentId
    ) {
        communityService.deleteComment(commentId, userVo.getId());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{communityId}/like")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "커뮤니티 좋아요 토글", description = "커뮤니티 게시글에 좋아요를 누르거나 취소합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "좋아요 토글 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글")
    })
    public ResponseEntity<Boolean> toggleLike(
        @AuthenticationPrincipal UserVo userVo,
        @Parameter(description = "게시글 ID") @PathVariable Long communityId
    ) {
        boolean isLiked = communityService.toggleLike(communityId, userVo.getId(), userVo.getUsername());
        return ResponseEntity.ok(isLiked);
    }
    
    @GetMapping("/{communityId}/like/status")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "좋아요 상태 확인", description = "사용자가 해당 게시글에 좋아요를 눌렀는지 확인합니다.")
    public ResponseEntity<Boolean> getLikeStatus(
        @AuthenticationPrincipal UserVo userVo,
        @Parameter(description = "게시글 ID") @PathVariable Long communityId
    ) {
        boolean isLiked = communityService.isLikedByUser(communityId, userVo.getId());
        return ResponseEntity.ok(isLiked);
    }
}