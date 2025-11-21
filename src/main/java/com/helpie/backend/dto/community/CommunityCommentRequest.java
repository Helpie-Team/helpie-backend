package com.helpie.backend.dto.community;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 커뮤니티 댓글 작성 요청 DTO
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Schema(description = "커뮤니티 댓글 작성 요청")
public record CommunityCommentRequest(
    
    @NotBlank(message = "댓글 내용은 필수입니다")
    @Size(max = 500, message = "댓글은 최대 500자까지 작성 가능합니다")
    @Schema(description = "댓글 내용", example = "좋은 정보 감사합니다!", requiredMode = Schema.RequiredMode.REQUIRED)
    String content
) {
}