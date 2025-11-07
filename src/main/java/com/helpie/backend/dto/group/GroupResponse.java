package com.helpie.backend.dto.group;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.group.GroupStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "소모임 응답 정보",
        example = """
        {  "id":1,
          "title": "영화 감상 모임",
          "description": "매주 영화를 보고 이야기 나누는 모임입니다",
          "cityName": "대한민국 > 서울",
          "category": "CULTURAL",
          "maxMember": 10,
          "thumbnail": null,
          "isPopular": false,
          "dayBefore": 25,
          "status": "RECRUITING",
          "meetingDate": "2025-12-01T19:00:00"
        }
        """)
public record GroupResponse(
    Long id,
    String title,
    String description,
    String cityName,
    Category category,
    Integer maxMember,
    String thumbnail,
    Boolean isPopular,
    Integer dayBefore,
    GroupStatus status,
    LocalDateTime meetingDate,
    Boolean isMarked
    ) {
    public static GroupResponse from(Group group,boolean isMarked) {
        return new GroupResponse(
            group.getId(),
            group.getTitle(),
            group.getDescription(),
            group.getCity().getCountry().getName() + " > " + group.getCity().getName(),
            group.getCategory(),
            group.getMaxMembers(),
            null,
            group.isPopular(),
            group.getDayBefore(),
            group.getStatus(),
            group.getMeetingDate(),
            isMarked
        );
    }


}
