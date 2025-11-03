package com.helpie.backend.controller.group;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.location.Country;
import com.helpie.backend.domain.user.UserRole;
import com.helpie.backend.domain.user.UserVo;
import com.helpie.backend.dto.group.GroupCreateRequest;
import com.helpie.backend.dto.group.GroupCreateResponse;
import com.helpie.backend.dto.group.GroupResponse;
import com.helpie.backend.service.group.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "소모임", description = "소모임 관련 API")
@RequestMapping("/api/v1/group")
public class GroupController {

    private final GroupService groupService;

    @PostMapping(value = "/create",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "소모임 등록", description = "소모임을 등록합니다")
    public ResponseEntity<GroupCreateResponse> createGroup(
        @AuthenticationPrincipal UserVo userVo,
        @Parameter(description = "소모임 생성 정보 (cityId는 도시 ID)") @RequestPart("payload") GroupCreateRequest request,
        @Parameter(description = "사진")@RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {

        GroupCreateResponse response = groupService.createGroup(userVo.getId(),request, images);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/interest")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    public ResponseEntity<Page<GroupResponse>> getGroupsByInterest(
        @AuthenticationPrincipal UserVo userVo,
        @RequestParam(defaultValue ="0") int page,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        Page<GroupResponse> response=groupService.getGroupsByInterest(userVo.getId(),pageable);
        return ResponseEntity.ok(response);
    }





}
