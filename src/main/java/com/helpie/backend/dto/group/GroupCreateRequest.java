package com.helpie.backend.dto.group;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.location.City;
import com.helpie.backend.domain.survey.Interest;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

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
    Integer maxMember,
    @Size(max=3,message = "이미지는 최대 3개까지 첨부할 수 있습니다.")
    List<MultipartFile> images
) {

    @Override
    public List<MultipartFile> images() {
        if (Objects.isNull(images)) {
            return Collections.emptyList();
        }
        return images;
    }

    public Group toEntity(City city, Long userId) {
        return Group.builder()
            .title(title)
            .description(description)
            .city(city)
            .category(category)
            .interests(interests)
            .maxMembers(maxMember)
            .meetingDate(meetingDate)
            .createdBy(userId)
            .build();
    }
}
