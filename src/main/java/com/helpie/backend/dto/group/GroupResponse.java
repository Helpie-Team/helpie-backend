package com.helpie.backend.dto.group;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.group.GroupStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "소모임 응답 정보",
        example = """
        {
          "title": "영화 감상 모임",
          "description": "매주 영화를 보고 이야기 나누는 모임입니다",
          "cityName": "서울",
          "category": "CULTURAL",
          "maxMember": 10,
          "thumbnail": null,
          "isPopular": false,
          "dayBefore": 5,
          "status": "RECRUITING"
        }
        """)
public record GroupResponse(
    String title,
    String description,
    String cityName,
    Category category,
    Integer maxMember,
    String thumbnail,
    Boolean isPopular,
    Integer dayBefore,
    GroupStatus status
    ) {
    public static GroupResponse from(Group group) {
        return new GroupResponse(
            group.getTitle(),
            group.getDescription(),
            group.getCity().getName(),
            group.getCategory(),
            group.getMaxMembers(),
            null,
            group.isPopular(),
            group.getDayBefore(),
            group.getStatus()
        );
    }


}
