package com.helpie.backend.controller.mypage;

import com.helpie.backend.domain.user.UserRole;
import com.helpie.backend.domain.user.UserVo;
import com.helpie.backend.dto.group.FindMyGroupsRequest;
import com.helpie.backend.dto.group.GroupResponse;
import com.helpie.backend.dto.group.MyGroupResponse;
import com.helpie.backend.dto.mypage.response.MyBookmarkResponse;
import com.helpie.backend.dto.mypage.response.MyProfileResponse;
import com.helpie.backend.dto.community.FindMyCommunitiesRequest;
import com.helpie.backend.dto.community.MyCommunityResponse;
import com.helpie.backend.dto.review.FindMyReviewsRequest;
import com.helpie.backend.dto.review.MyReviewResponse;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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


    @GetMapping("/bookmark-info")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @Operation(summary = "내 북마크 정보를 불러옵니다.")
    public ResponseEntity<Page<MyBookmarkResponse>> getMyBookmarkInfo(
            @AuthenticationPrincipal UserVo userVo,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(myPageFacade.getMyBookmarks(userVo.getId(), pageable));
    }


    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 사진 업데이트"
            )
    )
    @Operation(summary = "프로필 사진을 업데이트 합니다.")
    public ResponseEntity<Void> updateProfileImage(
            @AuthenticationPrincipal UserVo userVo,
            @RequestPart("profileImageFile") MultipartFile file
            ){
        myPageFacade.updateProfileImage(userVo.getId(), file);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/profile-image/reset")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 사진 초기화"
            )
    )
    @Operation(summary = "프로필 사진을 초기화 합니다.")
    public ResponseEntity<Void> resetProfileImage(
            @AuthenticationPrincipal UserVo userVo
    ){
        myPageFacade.resetProfileImage(userVo.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/profile-username")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 이름 변경 성공"
            )
    )
    @Operation(summary = "사용자 이름을 변경합니다.")
    public ResponseEntity<Void> updateProfileUsername(
            @AuthenticationPrincipal UserVo userVo,
            @RequestParam("username") String username
    ){
        myPageFacade.updateProfileUsername(userVo.getId(), username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/community-info")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @Operation(
        summary = "내 커뮤니티 정보를 조회합니다.",
        description = "내 커뮤니티 활동 통계(좋아요, 댓글, 게시글 수)와 작성한 게시글 목록을 함께 조회합니다.<br>" +
                     "소모임 정보 조회와 동일한 구조입니다."
    )
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = MyCommunityResponse.class)
    ))
    public ResponseEntity<MyCommunityResponse> getCommunityInfo(
            @AuthenticationPrincipal UserVo userVo,
            FindMyCommunitiesRequest request,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){

        return ResponseEntity.ok(myPageFacade.getMyCommunities(userVo.getId(), request.sort(), pageable));
    }

    @GetMapping("/review-info")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @Operation(
        summary = "내 리뷰 정보를 조회합니다.",
        description = "내 리뷰 활동 통계(총 리뷰 수, 평균 평점, 평점별 리뷰 수)와 작성한 리뷰 목록을 함께 조회합니다.<br>" +
                     "소모임 정보 조회와 동일한 구조입니다."
    )
    @ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = MyReviewResponse.class)
    ))
    public ResponseEntity<MyReviewResponse> getReviewInfo(
            @AuthenticationPrincipal UserVo userVo,
            FindMyReviewsRequest request,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){

        return ResponseEntity.ok(myPageFacade.getMyReviews(userVo.getId(), request.sort(), pageable));
    }
}
