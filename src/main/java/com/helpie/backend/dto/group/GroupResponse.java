package com.helpie.backend.dto.group;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.survey.Country;

public record GroupResponse(
    String title,
    String description,
    Country country,
    Category category,
    Integer maxMember,
    String thumbnail,
    Boolean isPopular
    ) {
    public static GroupResponse from(Group group) {
        return new GroupResponse(
            group.getTitle(),
            group.getDescription(),
            group.getCountry(),
            group.getCategory(),
            group.getMaxMembers(),
            null,
            group.isPopular()
        );
    }


}
