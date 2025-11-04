package com.helpie.backend.dto.group;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.group.Group;

public record GroupResponse(
    String title,
    String description,
    String cityName,
    Category category,
    Integer maxMember,
    String thumbnail,
    Boolean isPopular,
    Integer dayBefore
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
            group.getDayBefore()
        );
    }


}
