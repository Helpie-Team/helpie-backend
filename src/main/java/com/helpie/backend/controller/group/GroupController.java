package com.helpie.backend.controller.group;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.user.UserRole;
import com.helpie.backend.domain.user.UserVo;
import com.helpie.backend.dto.group.BookmarkResponse;
import com.helpie.backend.dto.group.GroupCreateRequest;
import com.helpie.backend.dto.group.GroupCreateResponse;
import com.helpie.backend.dto.group.GroupResponse;
import com.helpie.backend.dto.group.RecommendedResponse;
import com.helpie.backend.service.group.BookmarkService;
import com.helpie.backend.service.group.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "소모임-인증", description = "로그인한 사용자만 접근 가능한 API")
@RequestMapping("/api/v1/group")
public class GroupController {

    private final GroupService groupService;
    private final BookmarkService bookmarkService;

    @PostMapping(value = "/create",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "소모임 등록", 
               description = """
                           새로운 소모임을 생성합니다.
                           
                           **주요 필드:**
                           - cityId: 도시 ID (/api/v1/locations/cities에서 조회)
                           - endAt: 모집 마감 날짜시간 (이후 RECRUITMENT_CLOSED 상태로 변경)
                           - meetingDate: 실제 모임이 열리는 날짜시간 (참여자들이 모이는 시간)
                           - 생성 시 초기 상태: RECRUITING
                           
                           **상태 변화:**
                           1. RECRUITING (모집중) - 생성 시 기본 상태
                           2. RECRUITMENT_CLOSED (모집마감) - endAt 이후 또는 정원 달성 시
                           3. COMPLETED (모임완료) - 방장이 수동으로 완료 처리
                           """)
    public ResponseEntity<GroupCreateResponse> createGroup(
        @AuthenticationPrincipal UserVo userVo,
        @Parameter(description = "소모임 생성 정보 (cityId는 도시 ID)") @RequestPart("payload") GroupCreateRequest request,
        @Parameter(description = "사진")@RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {

        GroupCreateResponse response = groupService.createGroup(userVo.getId(),request, images);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/recommend")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    public ResponseEntity<RecommendedResponse> getGroupsByInterest(
        @AuthenticationPrincipal UserVo userVo,
        @RequestParam(defaultValue ="0") int page
    ){
        Pageable pageable= PageRequest.of(page,5,Sort.by("createdAt").descending());
        return ResponseEntity.ok(groupService.getGroupsByInterest(userVo.getId(),pageable));
    }

    @GetMapping("/list")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "국가,카테고리별 소모임 조회", description = "국가, 카테고리별로 소모임을 조회합니다")
    public ResponseEntity<Page<GroupResponse>> getGroupByCountry(
        @AuthenticationPrincipal UserVo userVo,
        @Parameter(description = "나라") @RequestParam String country,
        @Parameter(description = "소모임 카테고리") @RequestParam Category category,
        @RequestParam(defaultValue ="0") int page

    ){
        Pageable pageable= PageRequest.of(page,12,Sort.by("createdAt").descending());

        Page<GroupResponse> response=groupService.getGroupByCountry(userVo.getId(),country,category,pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mark/{groupId}")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "관심 소모임 등록/해제",
        description = """
    소모임을 관심 목록에 등록하거나 해제합니다.
    - **ADDED**: 관심 등록 완료
    - **REMOVED**: 관심 해제 완료
    """)
    public ResponseEntity<BookmarkResponse> toggleBookMark(
        @PathVariable Long groupId,
        @AuthenticationPrincipal UserVo userVo
    ){
        return ResponseEntity.ok(bookmarkService.toggleBookmark(userVo.getId(),groupId));
    }

    @GetMapping("/{groupId}")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "소모임 상세 정보 조회")
    public ResponseEntity<GroupResponse> getGroupById(
        @PathVariable Long groupId,
        @AuthenticationPrincipal UserVo userVo
    ){
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }

    @PostMapping("/join/{groupId}")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "소모임에 가입합니다")
    public ResponseEntity<String> joinGroup(@AuthenticationPrincipal UserVo userVo, @PathVariable Long groupId) {
        groupService.joinGroup(groupId,userVo.getId(), userVo.getUsername());
        return ResponseEntity.ok("소모임 가입에 완료되었습니다");
    }




    @PostMapping("/cancel/{groupId}")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "소모임 신청을 취소 합니다.")
    public ResponseEntity<String> cancelGroup(@AuthenticationPrincipal UserVo userVo, @PathVariable Long groupId) {
        groupService.cancelGroup(userVo.getId(), groupId);
        return ResponseEntity.ok("소모임 신청이 취소되었습니다.");
    }
}
