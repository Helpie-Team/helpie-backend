package com.helpie.backend.dto.review;

import java.util.List;

public record ReviewCreateResponse(
    Long id,
    Integer rate,
    String description,
    List<String> imageUrls
) {

}
