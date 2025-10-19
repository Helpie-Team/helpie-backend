package com.helpie.backend.exception.survey;

import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.ErrorCode;

/**
 * 설문조사 기본정보 중복 등록 예외
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
public class SurveyBasicInfoAlreadyExistsException extends BusinessException {
    
    public SurveyBasicInfoAlreadyExistsException(Long userId) {
        super(ErrorCode.SURVEY_BASIC_INFO_ALREADY_EXISTS, 
              "사용자 ID " + userId + "의 설문조사 기본정보가 이미 존재합니다.");
    }
}