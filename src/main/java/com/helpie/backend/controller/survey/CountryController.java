package com.helpie.backend.controller.survey;

import com.helpie.backend.domain.survey.Country;
import com.helpie.backend.dto.global.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 도시/지역 선택 관련 컨트롤러
 * 
 * @author 전우선
 * @since 2025-11-02(토)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/countries")
@Tag(name = "지역 선택", description = "도시/지역 선택 관련 API")
@RequiredArgsConstructor
public class CountryController {

    /**
     * 즐겨찾는 도시 5개를 조회합니다.
     */
    @GetMapping("/favorites")
    @Operation(summary = "즐겨찾는 도시 조회", 
               description = "전 세계 인기 도시 5개를 조회합니다. " +
                           "서울, 도쿄, 상하이, 로스앤젤레스, 런던이 포함됩니다. " +
                           "도시 선택 UI에서 우선 표시되는 도시들입니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public Response<List<CityResponse>> getFavoriteCities() {
        log.info("즐겨찾는 도시 조회 요청");
        
        List<CityResponse> favoriteCities = Country.getFavoriteCities()
                .stream()
                .map(city -> new CityResponse(city.name(), city.getDescription()))
                .toList();
        
        log.info("즐겨찾는 도시 조회 완료 - count: {}", favoriteCities.size());
        return Response.success(favoriteCities);
    }

    /**
     * 국가별로 그룹핑된 그 외 도시들을 조회합니다.
     */
    @GetMapping("/others")
    @Operation(summary = "그 외 도시 조회", 
               description = "국가별로 그룹핑된 전체 도시 목록을 조회합니다. " +
                           "미국(7개), 한국(6개), 중국(4개), 일본(3개), 영국(4개) 등 " +
                           "15개국 63개 도시가 국가별로 정리되어 있습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public Response<Map<String, List<CityResponse>>> getOtherCitiesByCountry() {
        log.info("그 외 도시 조회 요청");
        
        Map<String, List<CityResponse>> otherCities = Country.getOtherCitiesByCountry()
                .entrySet()
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue()
                            .stream()
                            .map(city -> new CityResponse(city.name(), city.getDescription()))
                            .toList(),
                    (existing, replacement) -> existing,
                    java.util.LinkedHashMap::new
                ));
        
        log.info("그 외 도시 조회 완료 - countries: {}", otherCities.size());
        return Response.success(otherCities);
    }

    /**
     * 도시 응답 DTO
     */
    @Schema(description = "도시 정보 응답")
    public record CityResponse(
        @Schema(description = "도시 코드", example = "SEOUL")
        String code,
        @Schema(description = "도시 이름", example = "서울")
        String name
    ) {}
}