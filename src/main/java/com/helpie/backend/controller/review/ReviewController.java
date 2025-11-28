package com.helpie.backend.controller.review;

import com.helpie.backend.domain.user.UserRole;
import com.helpie.backend.domain.user.UserVo;
import com.helpie.backend.dto.review.ReviewCreateRequest;
import com.helpie.backend.dto.review.ReviewCreateResponse;
import com.helpie.backend.dto.review.ReviewCheckResponse;
import com.helpie.backend.service.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/check/{groupId}")
    @Secured(UserRole.USER_TYPE)
    @SecurityRequirement(name = "JWT Authentication")
    @Operation(
        summary = "리뷰 작성 가능 여부 확인",
        description = """
                특정 그룹에 대한 사용자의 리뷰 작성 가능 여부를 확인합니다.
                
                **응답 필드:**
                - canWrite: 리뷰 작성 가능 여부 (true: 가능, false: 불가능)
                - hasReview: 사용자가 이미 리뷰를 작성했는지 여부
                - message: 상태 메시지
                
                **사용 예시:**
                - 그룹 상세 페이지 로드 시 호출하여 리뷰 작성 버튼 상태 결정
                - canWrite가 false이면 버튼 비활성화 또는 숨김 처리
                
                **참고:** 한 사용자당 하나의 그룹에 대해 리뷰를 한 번만 작성할 수 있습니다.
                """
    )
    @ApiResponse(
        responseCode = "200", 
        description = "성공",
        content = @Content(
            schema = @Schema(implementation = ReviewCheckResponse.class),
            examples = {
                @ExampleObject(
                    name = "리뷰 작성 가능",
                    summary = "사용자가 아직 리뷰를 작성하지 않은 경우",
                    value = """
                        {
                            "canWrite": true,
                            "hasReview": false,
                            "message": "리뷰 작성이 가능합니다"
                        }
                        """
                ),
                @ExampleObject(
                    name = "리뷰 작성 불가",
                    summary = "사용자가 이미 리뷰를 작성한 경우",
                    value = """
                        {
                            "canWrite": false,
                            "hasReview": true,
                            "message": "이미 리뷰를 작성했습니다"
                        }
                        """
                )
            }
        )
    )
    public ResponseEntity<ReviewCheckResponse> checkReviewStatus(
        @AuthenticationPrincipal UserVo userVo,
        @PathVariable Long groupId
    ) {
        boolean canWrite = reviewService.canUserWriteReview(userVo.getId(), groupId);
        ReviewCheckResponse response = ReviewCheckResponse.of(!canWrite);
        
        return ResponseEntity.ok(response);
    }
}
