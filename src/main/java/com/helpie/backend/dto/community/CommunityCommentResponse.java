package com.helpie.backend.dto.community;

import com.helpie.backend.domain.community.CommunityComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 커뮤니티 댓글 응답 DTO
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Builder
@Schema(description = "커뮤니티 댓글 응답")
public record CommunityCommentResponse(
    
    @Schema(description = "댓글 ID", example = "1")
    Long id,
    
    @Schema(description = "작성자 ID", example = "1")
    Long userId,
    
    @Schema(description = "작성자명", example = "홍길동")
    String username,
    
    @Schema(description = "작성자 프로필 이미지", example = "https://example.com/profile.jpg")
    String userProfileImage,
    
    @Schema(description = "댓글 내용", example = "좋은 정보 감사합니다!")
    String content,
    
    @Schema(description = "작성일시", example = "2025-11-21T10:30:00")
    LocalDateTime createdAt
) {
    
    /**
     * CommunityComment 엔티티로부터 응답 DTO 생성 (프로필 이미지 없음)
     */
    public static CommunityCommentResponse from(CommunityComment comment) {
        return CommunityCommentResponse.builder()
            .id(comment.getId())
            .userId(comment.getUserId())
            .username(comment.getUsername())
            .userProfileImage(null)
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .build();
    }
    
    /**
     * CommunityComment 엔티티로부터 응답 DTO 생성 (프로필 이미지 포함)
     */
    public static CommunityCommentResponse from(CommunityComment comment, String profileImage) {
        return CommunityCommentResponse.builder()
            .id(comment.getId())
            .userId(comment.getUserId())
            .username(comment.getUsername())
            .userProfileImage(profileImage)
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .build();
    }
}