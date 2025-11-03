package com.helpie.backend.service.location;

import com.helpie.backend.domain.location.City;
import com.helpie.backend.domain.location.Country;
import com.helpie.backend.dto.location.CityResponse;
import com.helpie.backend.dto.location.CountryResponse;
import com.helpie.backend.repository.location.CityRepository;
import com.helpie.backend.repository.location.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 지역(국가/도시) 관련 서비스 구현체
 * 
 * @author 전우선
 * @since 2025-11-03(일)
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    @Override
    public List<CityResponse> getFavoriteCities() {
        log.debug("즐겨찾는 도시 조회 시작");
        
        List<City> favoriteCities = cityRepository.findFavoriteCitiesOrderByDisplayOrder();
        
        List<CityResponse> responses = favoriteCities.stream()
                .map(CityResponse::from)
                .toList();
        
        log.debug("즐겨찾는 도시 조회 완료 - count: {}", responses.size());
        return responses;
    }

    @Override
    public Map<String, List<CityResponse>> getAllCitiesByCountry() {
        log.debug("국가별 전체 도시 조회 시작");
        
        List<City> allCities = cityRepository.findNonFavoriteCitiesOrderByCountryAndDisplayOrder();
        
        Map<String, List<CityResponse>> citiesByCountry = allCities.stream()
                .collect(Collectors.groupingBy(
                    city -> city.getCountry().getName(),
                    LinkedHashMap::new,
                    Collectors.mapping(CityResponse::from, Collectors.toList())
                ));
        
        log.debug("국가별 전체 도시 조회 완료 - countries: {}", citiesByCountry.size());
        return citiesByCountry;
    }

    @Override
    public List<CityResponse> getCitiesByCountryCode(String countryCode) {
        log.debug("국가별 도시 조회 시작 - countryCode: {}", countryCode);
        
        Country country = countryRepository.findByCode(countryCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 국가입니다: " + countryCode));
        
        List<City> cities = cityRepository.findByCountryOrderByDisplayOrder(country);
        
        List<CityResponse> responses = cities.stream()
                .map(CityResponse::from)
                .toList();
        
        log.debug("국가별 도시 조회 완료 - countryCode: {}, count: {}", countryCode, responses.size());
        return responses;
    }

    @Override
    public List<CountryResponse> getAllCountries() {
        log.debug("전체 국가 조회 시작");
        
        List<Country> countries = countryRepository.findAllOrderByName();
        
        List<CountryResponse> responses = countries.stream()
                .map(CountryResponse::from)
                .toList();
        
        log.debug("전체 국가 조회 완료 - count: {}", responses.size());
        return responses;
    }

    @Override
    public CityResponse getCityById(Long cityId) {
        log.debug("도시 조회 시작 - cityId: {}", cityId);
        
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도시입니다: " + cityId));
        
        CityResponse response = CityResponse.from(city);
        
        log.debug("도시 조회 완료 - cityId: {}, cityName: {}", cityId, response.name());
        return response;
    }
}