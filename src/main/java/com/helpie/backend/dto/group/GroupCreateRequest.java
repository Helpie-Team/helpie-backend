package com.helpie.backend.dto.group;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.survey.Interest;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "소모임 생성 요청",
        example = """
        {
          "title": "영화 감상 모임",
          "description": "매주 영화를 보고 이야기 나누는 모임입니다",
          "cityId": 1,
          "interests": ["MOVIE_WATCHING","BAKING"],
          "category": "HOBBY",
          "maxMember": 10,
          "meetingDate": "2025-12-01T19:00:00"
        }
        """)
public record GroupCreateRequest(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    String title,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    String description,
    @Schema(description = "소모임 위치 도시 ID (즐겨찾는 도시나 기타 도시 선택)", 
            example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Long cityId,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    Set<Interest> interests,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    Category category,
    @Schema(description = "모임 예정 날짜 및 시간 (실제 모임이 열리는 날짜)", 
            example = "2025-12-01T19:00:00", requiredMode = RequiredMode.REQUIRED)
    LocalDateTime meetingDate,
    @Schema(requiredMode = RequiredMode.REQUIRED)
    Integer maxMember
) {

}
