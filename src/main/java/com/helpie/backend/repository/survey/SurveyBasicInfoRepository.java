package com.helpie.backend.repository.survey;

import com.helpie.backend.domain.survey.SurveyBasicInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 설문조사 기본정보 데이터 접근 레이어
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
@Repository
public interface SurveyBasicInfoRepository extends JpaRepository<SurveyBasicInfo, Long> {
    
    Optional<SurveyBasicInfo> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
}