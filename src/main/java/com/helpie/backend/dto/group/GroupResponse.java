package com.helpie.backend.dto.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.group.GroupStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "소모임 응답 정보",
    example = """
        {
       
          "id": 1,
          "title": "영화 감상 모임",
          "description": "매주 영화를 보고 이야기 나누는 모임입니다",
          "cityName": "서울",
          "category": "CULTURAL",
          "maxMember": 10,
          "thumbnail": "https://kr.object.ncloudstorage.com/helpie-bucket/uploads/2025/11/07/uuid.png",
          "isPopular": false,
          "dayBefore": 25,
          "status": "RECRUITING",
          "meetingDate": "2025-12-01T19:00:00"
        }
        """)
public class GroupResponse {
    private Long id;
    private String title;
    private String description;
    private String cityName;
    private Category category;
    private Integer maxMember;
    private String thumbnail;
    private Boolean isPopular;
    private Integer dayBefore;
    private GroupStatus status;
    private LocalDateTime meetingDate;
    private Boolean isMarked;
    private static final String DEFAULT_THUMBNAIL_URL="https://kr.object.ncloudstorage.com/helpie-bucket/uploads/2025/11/10/62d39fbe-b94c-4b79-a606-84e80754f9fc.png";

    public static GroupResponse from(Group group) {
        return baseBuilder(group).build();
    }

    public static GroupResponse from(Group group, boolean isMarked) {
        return baseBuilder(group)
            .isMarked(isMarked)
            .build();
    }

    private static GroupResponseBuilder baseBuilder(Group group) {
        String thumbnail=group.getThumbnail();
        if (thumbnail == null) thumbnail=DEFAULT_THUMBNAIL_URL;

        int dayBefore;
        try {
            dayBefore = group.getDayBefore();
        } catch (com.helpie.backend.exception.GroupException e) {
            dayBefore = 0; // 끝난 모임은 0으로 처리
        }

        return GroupResponse.builder()
            .id(group.getId())
            .title(group.getTitle())
            .description(group.getDescription())
            .cityName(group.getCity().getName())
            .category(group.getCategory())
            .maxMember(group.getMaxMembers())
            .thumbnail(thumbnail)
            .isPopular(group.isPopular())
            .dayBefore(dayBefore)
            .status(group.getStatus())
            .meetingDate(group.getMeetingDate());
    }



}
