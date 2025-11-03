package com.helpie.backend.dto.group;

import com.helpie.backend.domain.survey.Interest;
import java.util.List;
import java.util.Set;

public record GroupCreateResponse(
    Long id,
    String title,
    String description,
    Integer maxMember,
    String city,
    Set<Interest> interest,
    List<String> imageUrls
) {

}
