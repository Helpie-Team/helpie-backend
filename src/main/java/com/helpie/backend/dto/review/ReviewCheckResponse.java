package com.helpie.backend.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "리뷰 작성 가능 여부 확인 응답")
public class ReviewCheckResponse {

    @Schema(description = "리뷰 작성 가능 여부", example = "false")
    private boolean canWrite;

    @Schema(description = "사용자가 이미 리뷰를 작성했는지 여부", example = "true")
    private boolean hasReview;

    @Schema(description = "상태 메시지", example = "이미 리뷰를 작성했습니다")
    private String message;

    public static ReviewCheckResponse of(boolean hasReview) {
        if (hasReview) {
            return ReviewCheckResponse.builder()
                    .canWrite(false)
                    .hasReview(true)
                    .message("이미 리뷰를 작성했습니다")
                    .build();
        } else {
            return ReviewCheckResponse.builder()
                    .canWrite(true)
                    .hasReview(false)
                    .message("리뷰 작성이 가능합니다")
                    .build();
        }
    }
}