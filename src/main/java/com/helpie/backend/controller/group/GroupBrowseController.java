package com.helpie.backend.controller.group;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.dto.group.GroupResponse;
import com.helpie.backend.service.group.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/group")
@Tag(name = "소모임 - 공개", description = "비로그인 사용자도 조회 가능한 API")
@RequiredArgsConstructor
public class GroupBrowseController {

    private final GroupService groupService;

    @GetMapping("/list")
    @Operation(summary = "국가,카테고리별 소모임 조회", description = "국가, 카테고리별로 소모임을 조회합니다")
    public ResponseEntity<Page<GroupResponse>> getGroupByCountry(
        @Parameter(description = "나라") @RequestParam String country,
        @Parameter(description = "소모임 카테고리") @RequestParam Category category,
        @RequestParam(defaultValue ="0") int page

    ){
        Pageable pageable= PageRequest.of(page,12,Sort.by("createdAt").descending());

        Page<GroupResponse> response=groupService.browseByCountry(country,category,pageable);
        return ResponseEntity.ok(response);
    }

}
