package com.helpie.backend.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "리뷰 생성 요청",
    example = """
        {
          "rate": "5,
          "description": "진짜 재밌는 소모임이었다..",
          "anonymityYn": false
        }
        """)
public record ReviewCreateRequest(
    Integer rate,
    String description,
    Boolean anonymityYn
) {

}

