package com.helpie.backend.controller.location;

import com.helpie.backend.dto.global.Response;
import com.helpie.backend.dto.location.CityResponse;
import com.helpie.backend.dto.location.CountryResponse;
import com.helpie.backend.service.location.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 도시/지역 선택 관련 컨트롤러
 * 
 * @author 전우선
 * @since 2025-11-03(일)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/locations")
@Tag(name = "지역 선택", description = "도시/지역 선택 관련 API")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    /**
     * 즐겨찾는 도시 5개를 조회합니다.
     */
    @GetMapping("/cities/favorites")
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
        
        List<CityResponse> favoriteCities = locationService.getFavoriteCities();
        
        log.info("즐겨찾는 도시 조회 완료 - count: {}", favoriteCities.size());
        return Response.success(favoriteCities);
    }

    /**
     * 국가별로 그룹핑된 모든 도시들을 조회합니다.
     */
    @GetMapping("/cities")
    @Operation(summary = "전체 도시 조회", 
               description = "국가별로 그룹핑된 전체 도시 목록을 조회합니다. " +
                           "미국(7개), 한국(6개), 중국(4개), 일본(3개), 영국(4개) 등 " +
                           "16개국 60여개 도시가 국가별로 정리되어 있습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public Response<Map<String, List<CityResponse>>> getAllCitiesByCountry() {
        log.info("전체 도시 조회 요청");
        
        Map<String, List<CityResponse>> citiesByCountry = locationService.getAllCitiesByCountry();
        
        log.info("전체 도시 조회 완료 - countries: {}", citiesByCountry.size());
        return Response.success(citiesByCountry);
    }

    /**
     * 특정 국가의 도시들을 조회합니다.
     */
    @GetMapping("/countries/{countryCode}/cities")
    @Operation(summary = "국가별 도시 조회", 
               description = "특정 국가의 모든 도시를 조회합니다. 토글 버튼 필터링에 사용됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 국가"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public Response<List<CityResponse>> getCitiesByCountry(
            @Parameter(description = "국가 코드", example = "KOREA")
            @PathVariable String countryCode) {
        
        log.info("국가별 도시 조회 요청 - countryCode: {}", countryCode);
        
        List<CityResponse> cities = locationService.getCitiesByCountryCode(countryCode);
        
        log.info("국가별 도시 조회 완료 - countryCode: {}, count: {}", countryCode, cities.size());
        return Response.success(cities);
    }

    /**
     * 모든 국가 목록을 조회합니다.
     */
    @GetMapping("/countries")
    @Operation(summary = "전체 국가 조회", description = "모든 국가 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public Response<List<CountryResponse>> getAllCountries() {
        log.info("전체 국가 조회 요청");
        
        List<CountryResponse> countries = locationService.getAllCountries();
        
        log.info("전체 국가 조회 완료 - count: {}", countries.size());
        return Response.success(countries);
    }
}