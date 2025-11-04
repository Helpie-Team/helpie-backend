package com.helpie.backend.dto.group;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public record RecommendedResponse(
    boolean isLocked,
    String reason,
    Page<GroupResponse> page
) {
    public static RecommendedResponse locked(String reason, Pageable pageable) {
        return new RecommendedResponse(true, reason, Page.empty(pageable));
    }
    public static RecommendedResponse ok(Page<GroupResponse> page) {
        return new RecommendedResponse(false, null, page);
    }

}
