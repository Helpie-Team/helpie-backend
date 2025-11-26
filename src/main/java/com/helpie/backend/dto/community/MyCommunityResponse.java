package com.helpie.backend.dto.community;

import com.helpie.backend.dto.mypage.response.MyCommunityActivityResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

/**
 * 마이페이지 커뮤니티 정보 통합 응답 DTO
 * 
 * @author 전우선
 * @since 2025-11-26(화)
 */
@Schema(description = "마이페이지 커뮤니티 통합 정보", example = """
    {
        "stats": {
            "likeCount": 15,
            "commentCount": 8,
            "postCount": 12
        },
        "activities": {
            "content": [
                {
                    "id": 1,
                    "thumbnailUrl": "https://example.com/image.jpg",
                    "categoryDisplayName": "정보공유",
                    "title": "유용한 정보 공유합니다",
                    "contentPreview": "안녕하세요! 오늘은 정말 유용한 정보를 공유하고 싶어서 글을 작성합니다...",
                    "createdAt": "2025-11-26T14:30:00",
                    "category": "INFO_SHARE"
                },
                {
                    "id": 2,
                    "thumbnailUrl": null,
                    "categoryDisplayName": "자유게시판",
                    "title": "이미지 없는 게시글",
                    "contentPreview": "이미지가 없는 게시글의 예시입니다...",
                    "createdAt": "2025-11-25T10:15:00",
                    "category": "FREE_BOARD"
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
            "totalElements": 12,
            "totalPages": 1,
            "last": true,
            "first": true,
            "numberOfElements": 12
        }
    }
    """)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyCommunityResponse {

    @Schema(description = "커뮤니티 활동 통계", example = """
        {
            "likeCount": 15,
            "commentCount": 8,
            "postCount": 12
        }
        """)
    private Stats stats;
    
    @Schema(description = "커뮤니티 활동 내역 (페이징)", example = """
        {
            "content": [
                {
                    "id": 1,
                    "thumbnailUrl": "https://example.com/image.jpg",
                    "categoryDisplayName": "정보공유",
                    "title": "유용한 정보 공유합니다",
                    "contentPreview": "안녕하세요! 오늘은 정말 유용한 정보를 공유하고 싶어서 글을 작성합니다...",
                    "createdAt": "2025-11-26T14:30:00",
                    "category": "INFO_SHARE"
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
            "totalElements": 12,
            "totalPages": 1,
            "last": true,
            "first": true,
            "numberOfElements": 12
        }
        """)
    private Page<MyCommunityActivityResponse> activities;

    @Schema(description = "커뮤니티 활동 통계 정보")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stats {
        @Schema(description = "받은 좋아요 수", example = "15")
        private Integer likeCount;

        @Schema(description = "받은 댓글 수", example = "8") 
        private Integer commentCount;

        @Schema(description = "작성한 게시글 수", example = "12")
        private Integer postCount;
    }
    
    public static MyCommunityResponse of(Integer likeCount, Integer commentCount, Integer postCount, 
                                       Page<MyCommunityActivityResponse> activities) {
        return new MyCommunityResponse(
            new Stats(likeCount, commentCount, postCount),
            activities
        );
    }
}