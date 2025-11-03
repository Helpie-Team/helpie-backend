package com.helpie.backend.dto.group;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.survey.Interest;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import java.util.Set;

@Schema(description = "소모임 생성 요청",
        example = """
        {
          "title": "영화 감상 모임",
          "description": "매주 영화를 보고 이야기 나누는 모임입니다",
          "cityId": 1,
          "interests": ["MOVIE_WATCHING", "DISCUSSION"],
          "category": "CULTURAL",
          "maxMember": 10
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
    @Schema(requiredMode = RequiredMode.REQUIRED)
    Integer maxMember
) {

}
