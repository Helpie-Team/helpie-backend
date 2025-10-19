package com.helpie.backend.exception.survey;

import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.ErrorCode;

/**
 * 설문조사 기본정보 조회 실패 예외
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
public class SurveyBasicInfoNotFoundException extends BusinessException {
    
    public SurveyBasicInfoNotFoundException(Long userId) {
        super(ErrorCode.SURVEY_BASIC_INFO_NOT_FOUND,
              "사용자 ID " + userId + "의 설문조사 기본정보를 찾을 수 없습니다.");
    }
}