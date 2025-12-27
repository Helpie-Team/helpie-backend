package com.helpie.backend.repository.location;

import com.helpie.backend.domain.location.City;
import com.helpie.backend.domain.location.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 도시 Repository
 * 
 * @author 전우선
 * @since 2025-11-03(일)
 */
public interface CityRepository extends JpaRepository<City, Long> {
    
    /**
     * 도시 코드로 조회
     */
    Optional<City> findByCode(String code);
    
    /**
     * 국가별 도시 조회 (표시 순서대로)
     */
    @Query("SELECT c FROM City c WHERE c.country = :country ORDER BY c.displayOrder, c.name")
    List<City> findByCountryOrderByDisplayOrder(@Param("country") Country country);
    
    /**
     * 국가 ID별 도시 조회
     */
    @Query("SELECT c FROM City c WHERE c.country.id = :countryId ORDER BY c.displayOrder, c.name")
    List<City> findByCountryIdOrderByDisplayOrder(@Param("countryId") Long countryId);
    
    /**
     * 즐겨찾는 도시 조회 (표시 순서대로)
     */
    @Query("SELECT c FROM City c WHERE c.isFavorite = true ORDER BY c.displayOrder, c.name")
    List<City> findFavoriteCitiesOrderByDisplayOrder();
    
    /**
     * 즐겨찾는 도시가 아닌 도시들을 국가별로 그룹핑하여 조회
     */
    @Query("SELECT c FROM City c WHERE c.isFavorite = false ORDER BY c.country.name, c.displayOrder, c.name")
    List<City> findNonFavoriteCitiesOrderByCountryAndDisplayOrder();
    
    /**
     * 특정 국가의 도시 개수 조회
     */
    @Query("SELECT COUNT(c) FROM City c WHERE c.country.id = :countryId")
    Long countByCountryId(@Param("countryId") Long countryId);

    Optional<City> findByCodeAndCountry(String code, Country country);

    List<City> findAllByCountry(Country country);
}