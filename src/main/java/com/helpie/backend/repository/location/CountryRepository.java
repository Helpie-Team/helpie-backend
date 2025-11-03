package com.helpie.backend.repository.location;

import com.helpie.backend.domain.location.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * 국가 Repository
 * 
 * @author 전우선
 * @since 2025-11-03(일)
 */
public interface CountryRepository extends JpaRepository<Country, Long> {
    
    /**
     * 국가 코드로 조회
     */
    Optional<Country> findByCode(String code);
    
    /**
     * 국가명으로 조회
     */
    Optional<Country> findByName(String name);
    
    /**
     * 모든 국가를 이름 순으로 조회
     */
    @Query("SELECT c FROM Country c ORDER BY c.name")
    List<Country> findAllOrderByName();
}