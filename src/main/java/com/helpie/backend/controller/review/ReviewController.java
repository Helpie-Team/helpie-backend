package com.helpie.backend.controller.review;

import com.helpie.backend.domain.user.UserRole;
import com.helpie.backend.domain.user.UserVo;
import com.helpie.backend.dto.review.ReviewCreateRequest;
import com.helpie.backend.dto.review.ReviewCreateResponse;
import com.helpie.backend.service.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "리뷰", description = "리뷰 관련 API")
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping(value = "/create/{groupId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(summary = "리뷰 등록",
        description = """
                            리뷰를 등록한다
                           
                           **주요 필드:**
                           - anonymityYn: 익명 여부(true: 익명, false: 실명)
                           - rate: 평점
                           - description: 후기
                           """)
    public ResponseEntity<ReviewCreateResponse> createReview(
        @AuthenticationPrincipal UserVo userVo,
        @PathVariable Long groupId,
        @Parameter(description = "리뷰 작성") @RequestPart("payload") ReviewCreateRequest request,
        @Parameter(description = "사진")@RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {

        ReviewCreateResponse response = reviewService.createReview(userVo.getId(),groupId,request, images);

        return ResponseEntity.ok(response);
    }
}
