package com.helpie.backend.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

/**
 * 마이페이지 리뷰 정보 통합 응답 DTO
 * 
 * @author 전우선
 * @since 2025-11-26(화)
 */
@Schema(description = "마이페이지 리뷰 통합 정보", example = """
    {
        "stats": {
            "totalReviews": 8,
            "averageRating": 4.5,
            "fiveStarCount": 5,
            "fourStarCount": 2,
            "threeStarCount": 1
        },
        "activities": {
            "content": [
                {
                    "id": 1,
                    "thumbnailUrl": "https://example.com/image.jpg",
                    "groupTitle": "헬스 동호회",
                    "rating": 5,
                    "reviewerName": "홍길동",
                    "meetingDate": "2025-10-24T19:00:00",
                    "contentPreview": "정말 좋은 모임이었습니다. 다들 친절하시고 운동도 열심히...",
                    "createdAt": "2025-11-26T14:30:00",
                    "isAnonymous": false
                }
            ],
            "pageable": {
                "pageNumber": 0,
                "pageSize": 20,
                "sort": {
                    "sorted": true,
                    "direction": "DESC",
                    "orderBy": ["createdAt"]
                }
            },
            "totalElements": 8,
            "totalPages": 1,
            "last": true,
            "first": true,
            "numberOfElements": 8
        }
    }
    """)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyReviewResponse {

    @Schema(description = "리뷰 활동 통계")
    private Stats stats;
    
    @Schema(description = "리뷰 활동 내역")
    private Page<MyReviewActivityResponse> activities;

    @Schema(description = "리뷰 활동 통계 정보")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stats {
        @Schema(description = "총 리뷰 수", example = "8")
        private Integer totalReviews;

        @Schema(description = "평균 평점", example = "4.5")
        private Double averageRating;

        @Schema(description = "5점 리뷰 수", example = "5")
        private Integer fiveStarCount;

        @Schema(description = "4점 리뷰 수", example = "2")
        private Integer fourStarCount;

        @Schema(description = "3점 리뷰 수", example = "1")
        private Integer threeStarCount;
    }
    
    public static MyReviewResponse of(Integer totalReviews, Double averageRating, 
                                    Integer fiveStarCount, Integer fourStarCount, Integer threeStarCount,
                                    Page<MyReviewActivityResponse> activities) {
        return new MyReviewResponse(
            new Stats(totalReviews, averageRating, fiveStarCount, fourStarCount, threeStarCount),
            activities
        );
    }
}