package com.helpie.backend.dto.group;

import com.helpie.backend.domain.group.GroupStatus;

public record FindMyGroupsRequest(
        GroupStatus status
) {
}
