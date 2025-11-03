package com.helpie.backend.dto.location;

import com.helpie.backend.domain.location.City;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 도시 응답 DTO
 * 
 * @author 전우선
 * @since 2025-11-03(일)
 */
@Schema(description = "도시 정보 응답")
public record CityResponse(
    @Schema(description = "도시 ID", example = "1")
    Long id,
    
    @Schema(description = "도시 코드", example = "SEOUL")
    String code,
    
    @Schema(description = "도시 이름", example = "서울")
    String name,
    
    @Schema(description = "도시 영문명", example = "Seoul")
    String englishName,
    
    @Schema(description = "국가 정보")
    CountryResponse country,
    
    @Schema(description = "즐겨찾는 도시 여부", example = "true")
    Boolean isFavorite
) {
    public static CityResponse from(City city) {
        return new CityResponse(
            city.getId(),
            city.getCode(),
            city.getName(),
            city.getEnglishName(),
            CountryResponse.from(city.getCountry()),
            city.getIsFavorite()
        );
    }
}