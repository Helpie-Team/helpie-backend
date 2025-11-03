package com.helpie.backend.dto.location;

import com.helpie.backend.domain.location.Country;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 국가 응답 DTO
 * 
 * @author 전우선
 * @since 2025-11-03(일)
 */
@Schema(description = "국가 정보 응답")
public record CountryResponse(
    @Schema(description = "국가 ID", example = "1")
    Long id,
    
    @Schema(description = "국가 코드", example = "KOREA")
    String code,
    
    @Schema(description = "국가 이름", example = "한국")
    String name,
    
    @Schema(description = "국가 영문명", example = "South Korea")
    String englishName
) {
    public static CountryResponse from(Country country) {
        return new CountryResponse(
            country.getId(),
            country.getCode(),
            country.getName(),
            country.getEnglishName()
        );
    }
}