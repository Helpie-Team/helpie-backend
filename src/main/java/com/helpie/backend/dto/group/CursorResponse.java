package com.helpie.backend.dto.group;

import java.time.LocalDateTime;
import java.util.List;


public record CursorResponse<T>(
    List<T> content,
    boolean hasNext,
    NextCursor nextCursor
) {
    public record NextCursor(LocalDateTime createdAt, Long id) {}
}

