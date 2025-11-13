package com.helpie.backend.controller.group;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.dto.group.GroupResponse;
import com.helpie.backend.dto.review.ReviewResponse;
import com.helpie.backend.service.group.GroupService;
import com.helpie.backend.service.review.ReviewService;
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
@RequestMapping("/api/v1/public")
@Tag(name = "공개 API", description = "비로그인 사용자도 조회 가능한 API")
@RequiredArgsConstructor
public class GroupBrowseController {

    private final GroupService groupService;
    private final ReviewService reviewService;

    @GetMapping("/group/list")
    @Operation(summary = "국가, 카테고리별 소모임 조회", description = "국가, 카테고리별로 소모임을 조회합니다")
    public ResponseEntity<Page<GroupResponse>> getGroupByCountry(
        @Parameter(description = "나라") @RequestParam String country,
        @Parameter(description = "소모임 카테고리") @RequestParam Category category,
        @RequestParam(defaultValue = "0") int page
    ) {
        Pageable pageable = PageRequest.of(page, 12, Sort.by("createdAt").descending());

        if (country.equals("ALL")){
            return ResponseEntity.ok(groupService.browseAllCountry(category, pageable));
        }
        return ResponseEntity.ok(groupService.browseByCountry(country, category, pageable));
    }

    @GetMapping("/review/list")
    @Operation(summary = "후기 조회", description = "후기를 조회합니다")
    public ResponseEntity<Page<ReviewResponse>> getReviews(
        @RequestParam(defaultValue ="0") int page
    ){
        Pageable pageable= PageRequest.of(page,3, Sort.by("createdAt").descending());
        return ResponseEntity.ok(reviewService.getReview(pageable));
    }

}
