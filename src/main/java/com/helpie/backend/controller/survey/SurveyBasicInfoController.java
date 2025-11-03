package com.helpie.backend.controller.survey;

import com.helpie.backend.domain.user.UserRole;
import com.helpie.backend.domain.user.UserVo;
import com.helpie.backend.dto.survey.SurveyBasicInfoRequest;
import com.helpie.backend.service.survey.SurveyBasicInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 설문조사 기본정보 관리 컨트롤러
 * 
 * @author 전우선
 * @since 2025-10-19(일)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/surveys")
@Tag(name = "설문조사 기본정보", description = "설문조사 기본정보 관리 API")
@RequiredArgsConstructor
public class SurveyBasicInfoController {

    private final SurveyBasicInfoService surveyBasicInfoService;

    /**
     * 설문조사 기본정보를 최초 저장합니다.
     * 중복 등록 시 409 Conflict 응답이 반환됩니다.
     */
    @PostMapping("/basic-info")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "설문조사 기본정보 저장", 
               description = "사용자의 기본 프로필 정보를 저장합니다. " +
                           "cityId는 /api/v1/locations/cities/favorites 또는 /api/v1/locations/cities에서 조회한 도시 ID를 사용합니다. " +
                           "즐겨찾는 도시(서울, 도쿄, 상하이, 로스앵젤레스, 런던) 또는 기타 도시 중 선택 가능합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "저장 성공"),
        @ApiResponse(responseCode = "400", description = "필수값 누락 또는 유효하지 않은 값"),
        @ApiResponse(responseCode = "409", description = "이미 등록된 사용자"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Void> saveSurveyBasicInfo(
            @AuthenticationPrincipal UserVo userVo,
            @Valid @RequestBody SurveyBasicInfoRequest request) {
        
        log.info("설문조사 기본정보 저장 요청 - userId: {}, cityId: {}, gender: {}", 
                 userVo.getId(), request.getCity(), request.getGender());
        
        surveyBasicInfoService.saveSurveyBasicInfo(userVo.getId(), request);
        
        log.info("설문조사 기본정보 저장 완료 - userId: {}", userVo.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * 기존 설문조사 기본정보를 수정합니다.
     * 등록된 정보가 없을 시 404 Not Found 응답이 반환됩니다.
     */
    @PutMapping("/basic-info")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "설문조사 기본정보 수정", 
               description = "기존에 등록된 설문조사 기본정보를 수정합니다. " +
                           "cityId는 /api/v1/locations/cities/favorites 또는 /api/v1/locations/cities에서 조회한 도시 ID를 사용합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "필수값 누락 또는 유효하지 않은 값"),
        @ApiResponse(responseCode = "404", description = "등록된 정보를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Void> updateSurveyBasicInfo(
            @AuthenticationPrincipal UserVo userVo,
            @Valid @RequestBody SurveyBasicInfoRequest request) {
        
        log.info("설문조사 기본정보 수정 요청 - userId: {}, cityId: {}, gender: {}", 
                 userVo.getId(), request.getCity(), request.getGender());
        
        surveyBasicInfoService.updateSurveyBasicInfo(userVo.getId(), request);
        
        log.info("설문조사 기본정보 수정 완료 - userId: {}", userVo.getId());
        return ResponseEntity.ok().build();
    }
}