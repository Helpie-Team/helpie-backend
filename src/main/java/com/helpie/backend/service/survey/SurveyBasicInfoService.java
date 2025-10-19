package com.helpie.backend.service.survey;

import com.helpie.backend.dto.survey.SurveyBasicInfoRequest;
import com.helpie.backend.exception.survey.SurveyBasicInfoAlreadyExistsException;
import com.helpie.backend.exception.survey.SurveyBasicInfoNotFoundException;

/**
 * 설문조사 기본정보 비즈니스 로직 서비스
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
public interface SurveyBasicInfoService {
    
    /**
     * 설문조사 기본정보를 최초 저장합니다.
     * @throws SurveyBasicInfoAlreadyExistsException 이미 등록된 사용자인 경우
     */
    void saveSurveyBasicInfo(Long userId, SurveyBasicInfoRequest request);
    
    /**
     * 기존 설문조사 기본정보를 수정합니다.
     * @throws SurveyBasicInfoNotFoundException 등록된 정보가 없는 경우
     */
    void updateSurveyBasicInfo(Long userId, SurveyBasicInfoRequest request);
}