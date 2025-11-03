package com.helpie.backend.dto.survey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.helpie.backend.domain.survey.AgeGroup;
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

    @Schema(description = "도시 ID (/api/v1/locations/cities/favorites 또는 /api/v1/locations/cities에서 조회)", 
            example = "1", required = true)
    @NotNull(message = "도시는 필수입니다.")
    private Long cityId;
    
    // Getter for backward compatibility (not exposed in JSON)
    @JsonIgnore
    public Long getCity() {
        return cityId;
    }

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