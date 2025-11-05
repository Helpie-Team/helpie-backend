package com.helpie.backend.service.survey;

import com.helpie.backend.domain.location.City;
import com.helpie.backend.domain.survey.SurveyBasicInfo;
import com.helpie.backend.dto.survey.SurveyBasicInfoRequest;
import com.helpie.backend.dto.survey.SurveyBasicInfoResponse;
import com.helpie.backend.exception.survey.SurveyBasicInfoAlreadyExistsException;
import com.helpie.backend.exception.survey.SurveyBasicInfoNotFoundException;
import com.helpie.backend.repository.location.CityRepository;
import com.helpie.backend.repository.survey.SurveyBasicInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 설문조사 기본정보 비즈니스 로직 서비스 구현체
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SurveyBasicInfoServiceImpl implements SurveyBasicInfoService {

    private final SurveyBasicInfoRepository surveyBasicInfoRepository;
    private final CityRepository cityRepository;

    @Override
    public void saveSurveyBasicInfo(Long userId, SurveyBasicInfoRequest request) {
        log.debug("설문조사 기본정보 저장 시작 - userId: {}", userId);
        
        validateUserNotExists(userId);
        SurveyBasicInfo surveyBasicInfo = createSurveyBasicInfo(userId, request);
        surveyBasicInfoRepository.save(surveyBasicInfo);
        
        log.info("설문조사 기본정보 저장 완료 - userId: {}", userId);
    }

    @Override
    public void updateSurveyBasicInfo(Long userId, SurveyBasicInfoRequest request) {
        log.debug("설문조사 기본정보 수정 시작 - userId: {}", userId);
        
        SurveyBasicInfo surveyBasicInfo = findSurveyBasicInfoByUserId(userId);
        updateSurveyBasicInfoData(surveyBasicInfo, request);
        
        log.info("설문조사 기본정보 수정 완료 - userId: {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public SurveyBasicInfoResponse getSurveyBasicInfo(Long userId) {
        log.debug("설문조사 기본정보 조회 시작 - userId: {}", userId);
        
        SurveyBasicInfo surveyBasicInfo = findSurveyBasicInfoByUserId(userId);
        
        log.info("설문조사 기본정보 조회 완료 - userId: {}", userId);
        return SurveyBasicInfoResponse.from(surveyBasicInfo);
    }

    /**
     * 사용자 중복 등록 여부를 검증합니다.
     */
    private void validateUserNotExists(Long userId) {
        if (surveyBasicInfoRepository.existsByUserId(userId)) {
            log.warn("설문조사 기본정보 중복 등록 시도 - userId: {}", userId);
            throw new SurveyBasicInfoAlreadyExistsException(userId);
        }
    }

    private SurveyBasicInfo createSurveyBasicInfo(Long userId, SurveyBasicInfoRequest request) {
        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도시입니다: " + request.getCityId()));
        
        return new SurveyBasicInfo(
                userId,
                city,
                request.getGender(),
                request.getAgeGroup(),
                request.getLanguages(),
                request.getInterests()
        );
    }

    private SurveyBasicInfo findSurveyBasicInfoByUserId(Long userId) {
        return surveyBasicInfoRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("설문조사 기본정보 조회 실패 - userId: {}", userId);
                    return new SurveyBasicInfoNotFoundException(userId);
                });
    }

    private void updateSurveyBasicInfoData(SurveyBasicInfo surveyBasicInfo, SurveyBasicInfoRequest request) {
        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도시입니다: " + request.getCityId()));
        
        surveyBasicInfo.updateBasicInfo(
                city,
                request.getGender(),
                request.getAgeGroup(),
                request.getLanguages(),
                request.getInterests()
        );
    }
}