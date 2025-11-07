package com.helpie.backend.dto.group;

import com.helpie.backend.domain.group.Category;

import java.time.LocalDateTime;

public record MyGroupResponse(
        Long groupId,
        String title,
        String description,
        String cityName,
        Integer currentMember,
        Integer maxMember,
        Category category,
        LocalDateTime meetingDate
) {
}
