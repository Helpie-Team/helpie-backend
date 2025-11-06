package com.helpie.backend.controller.mypage;

import com.helpie.backend.domain.user.UserRole;
import com.helpie.backend.domain.user.UserVo;
import com.helpie.backend.dto.group.FindMyGroupsRequest;
import com.helpie.backend.dto.group.MyGroupResponse;
import com.helpie.backend.dto.mypage.response.MyProfileResponse;
import com.helpie.backend.facade.mypage.MyPageFacade;
import com.helpie.backend.service.location.LocationService;
import com.helpie.backend.service.survey.SurveyBasicInfoService;
import com.helpie.backend.service.user.UserCommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/v1/my-page")
@Tag(name = "마이 페이지", description = "마이 페이지 관련 API")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageFacade myPageFacade;


    @GetMapping("/profile-info")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(
                    schema = @Schema(implementation = MyProfileResponse.class)
            ))
    })
    @Operation(summary = "내 프로필 정보를 조회합니다.")
    public MyProfileResponse getMyProfileInfo(@AuthenticationPrincipal UserVo userVo) {
        return myPageFacade.getMyProfileInfo(userVo.getId());
    }

    @GetMapping("/group-info")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @Operation(summary = "내 소모임 정보를 조회합니다.")
    public ResponseEntity<Page<MyGroupResponse>> getGroupInfo(
            @AuthenticationPrincipal UserVo userVo,
            FindMyGroupsRequest request,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){

        return ResponseEntity.ok(myPageFacade.getMyGroups(userVo.getId(), request.status(),pageable));
    }
}
