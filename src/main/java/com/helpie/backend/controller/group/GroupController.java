package com.helpie.backend.controller.group;

import com.helpie.backend.dto.group.GroupCreateRequest;
import com.helpie.backend.dto.group.GroupCreateResponse;
import com.helpie.backend.service.group.GroupService;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
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

}
