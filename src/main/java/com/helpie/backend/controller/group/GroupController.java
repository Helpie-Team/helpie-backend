package com.helpie.backend.controller.group;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.dto.group.GroupCreateRequest;
import com.helpie.backend.dto.group.GroupCreateResponse;
import com.helpie.backend.dto.group.GroupResponse;
import com.helpie.backend.service.group.GroupService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GroupCreateResponse> createGroup(
        @Parameter(description = "사용자 ID") @RequestParam Long userId,
        @Parameter(description = "소모임 생성 정보") @RequestPart("payload") GroupCreateRequest request,
        @Parameter(description = "사진")@RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {

        GroupCreateResponse response = groupService.createGroup(userId,request, images);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<GroupResponse>> getGroups(
        @Parameter(description = "소모임 카테고리") @RequestParam Category category,
        @Parameter(description = "사용자 ID") @RequestParam Long userId,
        @RequestParam(defaultValue ="0") int page,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        Page<GroupResponse> response=groupService.getGroups(userId,category,pageable);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/interest")
    public ResponseEntity<Page<GroupResponse>> getGroupsByInterest(
        @Parameter(description = "사용자 ID") @RequestParam Long userId,
        @RequestParam(defaultValue ="0") int page,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        Page<GroupResponse> response=groupService.getGroupsByInterest(userId,pageable);
        return ResponseEntity.ok(response);
    }





}
