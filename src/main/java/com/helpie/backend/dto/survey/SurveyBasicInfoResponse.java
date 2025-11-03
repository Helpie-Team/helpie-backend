package com.helpie.backend.dto.survey;

import com.helpie.backend.domain.survey.AgeGroup;
import com.helpie.backend.domain.location.City;
import com.helpie.backend.domain.survey.Gender;
import com.helpie.backend.domain.survey.Interest;
import com.helpie.backend.domain.survey.Language;
import com.helpie.backend.domain.survey.SurveyBasicInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 설문조사 기본정보 응답 DTO
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyBasicInfoResponse {
    
    private Long id;
    private Long userId;
    private String cityName;
    private Long cityId;
    private Gender gender;
    private AgeGroup ageGroup;
    private Set<Language> languages;
    private Set<Interest> interests;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static SurveyBasicInfoResponse from(SurveyBasicInfo surveyBasicInfo) {
        return new SurveyBasicInfoResponse(
                surveyBasicInfo.getId(),
                surveyBasicInfo.getUserId(),
                surveyBasicInfo.getCity().getName(),
                surveyBasicInfo.getCity().getId(),
                surveyBasicInfo.getGender(),
                surveyBasicInfo.getAgeGroup(),
                surveyBasicInfo.getLanguages(),
                surveyBasicInfo.getInterests(),
                surveyBasicInfo.getCreatedAt(),
                surveyBasicInfo.getUpdatedAt()
        );
    }
}