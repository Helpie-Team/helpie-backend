package com.helpie.backend.dto.survey;

import com.helpie.backend.domain.survey.AgeGroup;
import com.helpie.backend.domain.survey.Country;
import com.helpie.backend.domain.survey.Gender;
import com.helpie.backend.domain.survey.Interest;
import com.helpie.backend.domain.survey.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * 설문조사 기본정보 요청 DTO
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
@Schema(description = "설문조사 기본정보 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyBasicInfoRequest {

    @Schema(description = "국가", example = "KOREA", required = true)
    @NotNull(message = "나라는 필수입니다.")
    private Country country;

    @Schema(description = "성별", example = "MALE", required = true)
    @NotNull(message = "성별은 필수입니다.")
    private Gender gender;

    @Schema(description = "연령대", example = "TWENTIES", required = true)
    @NotNull(message = "나이대는 필수입니다.")
    private AgeGroup ageGroup;

    @Schema(description = "사용 언어 목록", example = "[\"KOREAN\", \"ENGLISH\"]", required = true)
    @NotEmpty(message = "최소 하나의 언어를 선택해야 합니다.")
    private Set<Language> languages;

    @Schema(description = "관심사 목록", example = "[\"MOVIE_WATCHING\", \"EXERCISE\", \"TRAVEL\"]", required = true)
    @NotEmpty(message = "최소 하나의 관심사를 선택해야 합니다.")
    private Set<Interest> interests;
}