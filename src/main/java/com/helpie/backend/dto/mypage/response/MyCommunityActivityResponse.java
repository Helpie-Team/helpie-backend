package com.helpie.backend.dto.mypage.response;

import com.helpie.backend.domain.community.Community;
import com.helpie.backend.domain.community.CommunityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 마이페이지 커뮤니티 활동 내역 응답 DTO
 * 
 * @author 전우선
 * @since 2025-11-25(월)
 */
@Schema(description = "마이페이지 커뮤니티 활동 내역")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyCommunityActivityResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long id;

    @Schema(description = "썸네일 이미지 URL (없으면 null)", example = "https://example.com/image.jpg")
    private String thumbnailUrl;

    @Schema(description = "게시판 이름", example = "정보공유")
    private String categoryDisplayName;

    @Schema(description = "게시물 제목", example = "게시글 타이틀이 들어가는 내용입니다.")
    private String title;

    @Schema(description = "게시물 내용 미리보기", example = "게시글 본문이 들어가는 내용입니다. 두 줄 이상부터는 생략합니다. 두 줄 이상부터는 생략합니다....")
    private String contentPreview;

    @Schema(description = "작성 시간", example = "2020-02-05T14:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "카테고리", example = "INFO_SHARE")
    private CommunityCategory category;

    public static MyCommunityActivityResponse from(Community community, String thumbnailUrl) {
        // 내용 미리보기 생성 (100자 제한)
        String contentPreview = community.getContent().length() > 100 
            ? community.getContent().substring(0, 100) + "..."
            : community.getContent();

        return new MyCommunityActivityResponse(
            community.getId(),
            thumbnailUrl,
            community.getCategory().getDisplayName(),
            community.getTitle(),
            contentPreview,
            community.getCreatedAt(),
            community.getCategory()
        );
    }
}