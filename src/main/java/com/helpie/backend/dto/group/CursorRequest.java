package com.helpie.backend.dto.group;

import com.helpie.backend.domain.group.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CursorRequest {
    @Schema(description = "나라", example = "KOREA", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String country;

    @Schema(description = "카테고리", example = "HOBBY", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Category category;

    @Schema(description = "커서 createdAt", example = "2025-12-01T19:00:00")
    private LocalDateTime cursorCreatedAt;

    @Schema(description = "커서 id", example = "123")
    private Long cursorId;

    @Schema(description = "페이지 크기", example = "20", defaultValue = "20")
    @Min(1)
    private int size = 20;

}
