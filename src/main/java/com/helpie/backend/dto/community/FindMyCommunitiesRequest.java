package com.helpie.backend.dto.community;

import io.swagger.v3.oas.annotations.media.Schema;

public record FindMyCommunitiesRequest(
        @Schema(
                description = "조회할 정렬 기준.<br>" +
                        "- <code>latest</code>: 최신순<br>" +
                        "- <code>likes</code>: 좋아요순<br>" +
                        "값이 없으면 최신순으로 조회합니다.",
                example = "latest"
        )
        String sort
) {
}