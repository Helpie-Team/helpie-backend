package com.helpie.backend.dto.mypage.response;


import com.helpie.backend.dto.location.CityResponse;
import com.helpie.backend.dto.survey.SurveyBasicInfoResponse;

public record MyProfileResponse(
    String username,
    String email,
    Boolean surveyStatus,
    String imageUrl,
    SurveyBasicInfoResponse surveyBasicInfo,
    CityResponse city
) {
}
