package com.helpie.backend.dto.community;

import com.helpie.backend.domain.community.Community;
import com.helpie.backend.domain.community.CommunityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 커뮤니티 게시글 응답 DTO
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Schema(description = "커뮤니티 게시글 응답")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long id;

    @Schema(description = "작성자 ID", example = "123")
    private Long userId;

    @Schema(description = "작성자 이름", example = "홍길동")
    private String username;

    @Schema(description = "작성자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String userProfileImage;

    @Schema(description = "카테고리 (ALL: 전체, INFO_SHARE: 정보공유, FREE_BOARD: 자유게시판)", example = "INFO_SHARE")
    private CommunityCategory category;

    @Schema(description = "카테고리 표시명", example = "정보공유")
    private String categoryDisplayName;

    @Schema(description = "제목", example = "도움이 되는 정보입니다")
    private String title;

    @Schema(description = "내용", example = "게시글 내용입니다")
    private String content;

    @Schema(description = "첨부 이미지 목록")
    private List<String> imageUrls;

    @Schema(description = "조회수", example = "42")
    private Integer viewCount;

    @Schema(description = "좋아요 수", example = "15")
    private Integer likesCount;

    @Schema(description = "댓글 수", example = "7")
    private Integer commentsCount;

    @Schema(description = "작성일시", example = "2025-11-21T14:30:28")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2025-11-21T14:30:28")
    private LocalDateTime updatedAt;

    public static CommunityResponse from(Community community, String userProfileImage) {
        return new CommunityResponse(
            community.getId(),
            community.getUserId(),
            community.getUsername(),
            userProfileImage,
            community.getCategory(),
            community.getCategory().getDisplayName(),
            community.getTitle(),
            community.getContent(),
            community.getImages().stream()
                .map(image -> image.getImageUrl())
                .collect(Collectors.toList()),
            community.getViewCount(),
            community.getLikesCount(),
            community.getCommentsCount(),
            community.getCreatedAt(),
            community.getUpdatedAt()
        );
    }

    public static CommunityResponse fromSummary(Community community, String userProfileImage) {
        return new CommunityResponse(
            community.getId(),
            community.getUserId(),
            community.getUsername(),
            userProfileImage,
            community.getCategory(),
            community.getCategory().getDisplayName(),
            community.getTitle(),
            truncateContent(community.getContent(), 100),
            community.getImages().stream()
                .map(image -> image.getImageUrl())
                .collect(Collectors.toList()),
            community.getViewCount(),
            community.getLikesCount(),
            community.getCommentsCount(),
            community.getCreatedAt(),
            community.getUpdatedAt()
        );
    }

    private static String truncateContent(String content, int maxLength) {
        if (content == null) return "";
        if (content.length() <= maxLength) return content;
        return content.substring(0, maxLength) + "...";
    }
}