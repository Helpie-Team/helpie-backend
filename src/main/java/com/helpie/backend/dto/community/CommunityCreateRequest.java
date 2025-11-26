package com.helpie.backend.dto.community;

import com.helpie.backend.domain.community.CommunityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 커뮤니티 게시글 작성 요청 DTO
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Schema(description = "커뮤니티 게시글 작성 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityCreateRequest {

    @Schema(description = "카테고리 (INFO_SHARE: 정보공유, FREE_BOARD: 자유게시판)", example = "INFO_SHARE", allowableValues = {"INFO_SHARE", "FREE_BOARD"})
    @NotNull(message = "카테고리는 필수입니다")
    private CommunityCategory category;

    @Schema(description = "제목", example = "도움이 되는 정보입니다")
    @NotBlank(message = "제목은 필수입니다")
    @Size(min = 1, max = 200, message = "제목은 1자 이상 200자 이하로 입력해주세요")
    private String title;

    @Schema(description = "내용", example = "게시글 내용입니다")
    @NotBlank(message = "내용은 필수입니다")
    @Size(min = 1, max = 10000, message = "내용은 1자 이상 10000자 이하로 입력해주세요")
    private String content;
}