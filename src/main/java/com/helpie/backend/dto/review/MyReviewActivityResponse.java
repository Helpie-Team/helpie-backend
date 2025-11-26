package com.helpie.backend.dto.review;

import com.helpie.backend.domain.review.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 마이페이지 리뷰 활동 내역 응답 DTO
 * 
 * @author 전우선
 * @since 2025-11-26(화)
 */
@Schema(description = "마이페이지 리뷰 활동 내역")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyReviewActivityResponse {

    @Schema(description = "리뷰 ID", example = "1")
    private Long id;

    @Schema(description = "썸네일 이미지 URL (없으면 null)", example = "https://example.com/image.jpg")
    private String thumbnailUrl;

    @Schema(description = "소모임 제목", example = "헬스 동호회")
    private String groupTitle;

    @Schema(description = "평점", example = "5")
    private Integer rating;

    @Schema(description = "리뷰 작성자 이름 (본인 별명)", example = "홍길동")
    private String reviewerName;

    @Schema(description = "모임 참여 날짜", example = "2025-10-24T19:00:00")
    private LocalDateTime meetingDate;

    @Schema(description = "리뷰 내용 미리보기", example = "정말 좋은 모임이었습니다. 다들 친절하시고 운동도 열심히...")
    private String contentPreview;

    @Schema(description = "리뷰 작성 시간", example = "2025-11-26T14:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "익명 여부", example = "false")
    private Boolean isAnonymous;

    public static MyReviewActivityResponse from(Review review, String thumbnailUrl, String groupTitle, 
                                              String reviewerName, LocalDateTime meetingDate) {
        // 리뷰 내용 미리보기 생성 (100자 제한)
        String contentPreview = review.getDescription() != null && review.getDescription().length() > 100 
            ? review.getDescription().substring(0, 100) + "..."
            : review.getDescription();

        return new MyReviewActivityResponse(
            review.getId(),
            thumbnailUrl,
            groupTitle,
            review.getRate(),
            reviewerName,
            meetingDate,
            contentPreview,
            review.getCreatedAt(),
            review.getAnonymityYn()
        );
    }
}