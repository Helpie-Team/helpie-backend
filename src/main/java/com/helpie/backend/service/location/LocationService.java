package com.helpie.backend.service.location;

import com.helpie.backend.dto.location.CityResponse;
import com.helpie.backend.dto.location.CountryResponse;

import java.util.List;
import java.util.Map;

/**
 * 지역(국가/도시) 관련 서비스 인터페이스
 * 
 * @author 전우선
 * @since 2025-11-03(일)
 */
public interface LocationService {
    
    /**
     * 즐겨찾는 도시 목록 조회
     */
    List<CityResponse> getFavoriteCities();
    
    /**
     * 국가별로 그룹핑된 전체 도시 목록 조회
     */
    Map<String, List<CityResponse>> getAllCitiesByCountry();
    
    /**
     * 특정 국가의 도시 목록 조회
     */
    List<CityResponse> getCitiesByCountryCode(String countryCode);
    
    /**
     * 전체 국가 목록 조회
     */
    List<CountryResponse> getAllCountries();
    
    /**
     * 도시 ID로 도시 정보 조회
     */
    CityResponse getCityById(Long cityId);
}