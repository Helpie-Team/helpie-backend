package com.helpie.backend.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;

public record FindMyReviewsRequest(
        @Schema(
                description = "조회할 정렬 기준.<br>" +
                        "- <code>latest</code>: 최신순<br>" +
                        "- <code>rating</code>: 평점순<br>" +
                        "값이 없으면 최신순으로 조회합니다.",
                example = "latest"
        )
        String sort
) {
}