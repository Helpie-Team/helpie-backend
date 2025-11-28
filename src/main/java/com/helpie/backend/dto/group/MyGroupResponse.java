package com.helpie.backend.dto.group;

import com.helpie.backend.domain.group.Category;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "내가 참여한 소모임 정보 응답 DTO")
public record MyGroupResponse(
        @Schema(description = "소모임 ID", example = "12")
        Long groupId,
        
        @Schema(description = "소모임 이름", example = "헬스 동호회")
        String title,
        
        @Schema(description = "소모임 설명", example = "주 3회 운동 모임")
        String description,
        
        @Schema(description = "도시 이름", example = "서울")
        String cityName,
        
        @Schema(description = "현재 인원", example = "8")
        Integer currentMember,
        
        @Schema(description = "최대 인원", example = "15")
        Integer maxMember,
        
        @Schema(description = "카테고리", example = "사회/친목")
        Category category,
        
        @Schema(description = "모임 날짜")
        LocalDateTime meetingDate,
        
        @Schema(description = "썸네일 URL (없으면 null)", example = "https://example.com/thumbnail.jpg")
        String thumbnailUrl
) {
}
