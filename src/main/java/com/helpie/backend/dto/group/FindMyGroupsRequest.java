package com.helpie.backend.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;

public record FindMyGroupsRequest(
        @Schema(
                description = "조회할 모임 상태.<br>" +
                        "- <code>past</code>: 지난 모임<br>" +
                        "- <code>upcoming</code>: 예정 모임<br>" +
                        "값이 없으면 전체 모임을 조회합니다.",
                example = "upcoming"
        )
        String status
) {
}
