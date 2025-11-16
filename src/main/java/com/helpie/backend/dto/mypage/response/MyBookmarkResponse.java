package com.helpie.backend.dto.mypage.response;


import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.group.Group;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "내가 북마크한 소모임 정보 응답 DTO")
public record MyBookmarkResponse(

        @Schema(description = "소모임 ID", example = "12")
        Long id,

        @Schema(description = "소모임 이름", example = "헬스 동호회")
        String title,

        @Schema(description = "소모임 설명", example = "주 3회 운동 모임")
        String description,

        @Schema(description = "소모임 최대 인원", example = "15")
        int maxMembers,

        @Schema(description = "HOT 여부", example = "hot")
        boolean isPopular,

        @Schema(description = "도시 이름", example = "서울")
        String cityName,

        @Schema(description = "카테 고리", example = "사회/친목")
        Category category,

        @Schema(description = "모임 날짜")
        LocalDateTime meetingDate,

        @Schema(description = "모임 생성일")
        LocalDateTime createdAt,

        @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.jpg")
        String thumbnailUrl
) {

    public static MyBookmarkResponse from(Group group) {
        return new MyBookmarkResponse(
                group.getId(),
                group.getTitle(),
                group.getDescription(),
                group.getMaxMembers(),
                group.isPopular(),
                group.getCity().getName(),
                group.getCategory(),
                group.getMeetingDate(),
                group.getCreatedAt(),
                group.getThumbnail()
        );
    }
}

