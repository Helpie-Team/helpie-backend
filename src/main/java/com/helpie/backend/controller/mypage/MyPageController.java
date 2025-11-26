package com.helpie.backend.controller.mypage;

import com.helpie.backend.domain.user.UserRole;
import com.helpie.backend.domain.user.UserVo;
import com.helpie.backend.dto.group.FindMyGroupsRequest;
import com.helpie.backend.dto.group.MyGroupResponse;
import com.helpie.backend.dto.mypage.response.MyBookmarkResponse;
import com.helpie.backend.dto.mypage.response.MyProfileResponse;
import com.helpie.backend.dto.mypage.response.MyCommunityActivityResponse;
import com.helpie.backend.dto.review.MyReviewActivityResponse;
import com.helpie.backend.facade.mypage.MyPageFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController()
@RequestMapping("/api/v1/my-page")
@Tag(name = "마이 페이지", description = "마이 페이지 관련 API")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageFacade myPageFacade;

    @GetMapping("/profile-info")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(
                    schema = @Schema(implementation = MyProfileResponse.class)
            ))
    })
    @Operation(summary = "내 프로필 정보를 조회합니다.")
    public MyProfileResponse getMyProfileInfo(@AuthenticationPrincipal UserVo userVo) {
        return myPageFacade.getMyProfileInfo(userVo.getId());
    }

    @GetMapping("/group-info")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @Operation(
        summary = "내 소모임 목록을 조회합니다.", 
        description = "마이페이지 > 나의활동 > 소모임 탭<br>" +
                     "내가 참여한 소모임 목록을 조회합니다. (과거/예정 필터링 가능)"
    )
    public ResponseEntity<Page<MyGroupResponse>> getGroupInfo(
            @AuthenticationPrincipal UserVo userVo,
            FindMyGroupsRequest request,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){

        return ResponseEntity.ok(myPageFacade.getMyGroups(userVo.getId(), request.status(),pageable));
    }


    @GetMapping("/bookmark-info")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @Operation(summary = "내 북마크 정보를 불러옵니다.")
    public ResponseEntity<Page<MyBookmarkResponse>> getMyBookmarkInfo(
            @AuthenticationPrincipal UserVo userVo,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(myPageFacade.getMyBookmarks(userVo.getId(), pageable));
    }


    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 사진 업데이트"
            )
    )
    @Operation(summary = "프로필 사진을 업데이트 합니다.")
    public ResponseEntity<Void> updateProfileImage(
            @AuthenticationPrincipal UserVo userVo,
            @RequestPart("profileImageFile") MultipartFile file
            ){
        myPageFacade.updateProfileImage(userVo.getId(), file);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/profile-image/reset")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 사진 초기화"
            )
    )
    @Operation(summary = "프로필 사진을 초기화 합니다.")
    public ResponseEntity<Void> resetProfileImage(
            @AuthenticationPrincipal UserVo userVo
    ){
        myPageFacade.resetProfileImage(userVo.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/profile-username")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 이름 변경 성공"
            )
    )
    @Operation(summary = "사용자 이름을 변경합니다.")
    public ResponseEntity<Void> updateProfileUsername(
            @AuthenticationPrincipal UserVo userVo,
            @RequestParam("username") String username
    ){
        myPageFacade.updateProfileUsername(userVo.getId(), username);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/my-likes")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @Operation(
        summary = "내가 좋아요 누른 커뮤니티 게시글 목록을 조회합니다.",
        description = "마이페이지 > 나의활동 > '공감 3' 탭<br>" +
                     "내가 좋아요를 누른 커뮤니티 게시글 목록을 최신 좋아요순으로 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(
            schema = @Schema(implementation = Page.class),
            examples = @ExampleObject(value = """
                {
                    "content": [
                        {
                            "id": 1,
                            "thumbnailUrl": "https://example.com/image.jpg",
                            "categoryDisplayName": "정보공유",
                            "title": "내가 좋아요 누른 게시글",
                            "contentPreview": "이 게시글에 좋아요를 눌렀습니다...",
                            "createdAt": "2025-11-25T14:30:00",
                            "category": "INFO_SHARE"
                        },
                        {
                            "id": 2,
                            "thumbnailUrl": null,
                            "categoryDisplayName": "자유게시판",
                            "title": "또 다른 좋아요 누른 게시글",
                            "contentPreview": "여기에도 좋아요를 눌렀네요...",
                            "createdAt": "2025-11-24T09:15:00",
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
                    "totalElements": 2,
                    "totalPages": 1,
                    "last": true,
                    "first": true,
                    "numberOfElements": 2
                }
                """)
    ))
    public ResponseEntity<Page<MyCommunityActivityResponse>> getLikedCommunities(
            @AuthenticationPrincipal UserVo userVo,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){

        return ResponseEntity.ok(myPageFacade.getMyLikedCommunities(userVo.getId(), pageable));
    }

    @GetMapping("/my-reviews")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @Operation(
        summary = "내가 작성한 리뷰 목록을 조회합니다.",
        description = "마이페이지 > 나의활동 > '내 게시글' > '리뷰' 하위탭<br>" +
                     "내가 작성한 리뷰 목록을 최신순으로 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(
            schema = @Schema(implementation = Page.class),
            examples = @ExampleObject(value = """
                {
                    "content": [
                        {
                            "id": 1,
                            "thumbnailUrl": "https://example.com/review1.jpg",
                            "groupTitle": "헬스 동호회",
                            "reviewerName": "홍길동",
                            "content": "정말 좋은 모임이었습니다. 운동도 열심히 하고...",
                            "meetingDate": "2025-11-20T19:00:00",
                            "createdAt": "2025-11-21T10:30:00",
                            "rating": 5
                        },
                        {
                            "id": 2,
                            "thumbnailUrl": null,
                            "groupTitle": "독서 클럽",
                            "reviewerName": "익명사용자123",
                            "content": "책에 대한 다양한 의견을 나눌 수 있어서 좋았어요.",
                            "meetingDate": "2025-11-15T14:00:00",
                            "createdAt": "2025-11-16T09:15:00",
                            "rating": 4
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
                    "totalElements": 2,
                    "totalPages": 1,
                    "last": true,
                    "first": true,
                    "numberOfElements": 2
                }
                """)
    ))
    public ResponseEntity<Page<MyReviewActivityResponse>> getMyReviews(
            @AuthenticationPrincipal UserVo userVo,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){

        return ResponseEntity.ok(myPageFacade.getMyReviewActivities(userVo.getId(), pageable));
    }

    @GetMapping("/my-posts/groups")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @Operation(
        summary = "내가 만든 소모임 목록을 조회합니다.",
        description = "마이페이지 > 나의활동 > '내 게시글' > '소모임' 하위탭<br>" +
                     "내가 생성/작성한 소모임 목록을 최신순으로 조회합니다.<br>" +
                     "⚠️ 내가 가입한 소모임이 아닌, 내가 만든(생성한) 소모임만 조회됩니다."
    )
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(
            schema = @Schema(implementation = Page.class),
            examples = @ExampleObject(value = """
                {
                    "content": [
                        {
                            "groupId": 1,
                            "title": "[내가 만든] 헬스 동호회",
                            "description": "내가 호스트로 운영하는 주 3회 운동 모임",
                            "cityName": "서울",
                            "currentMember": 8,
                            "maxMember": 15,
                            "category": "SPORTS",
                            "meetingDate": "2025-12-01T19:00:00",
                            "thumbnailUrl": "https://example.com/image.jpg"
                        },
                        {
                            "groupId": 2,
                            "title": "[내가 만든] 독서 클럽",
                            "description": "내가 개설한 매주 토요일 독서 모임",
                            "cityName": "부산",
                            "currentMember": 5,
                            "maxMember": 10,
                            "category": "CULTURAL",
                            "meetingDate": "2025-11-30T14:00:00",
                            "thumbnailUrl": null
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
                    "totalElements": 2,
                    "totalPages": 1,
                    "last": true,
                    "first": true,
                    "numberOfElements": 2
                }
                """)
    ))
    public ResponseEntity<Page<MyGroupResponse>> getMyPostGroups(
            @AuthenticationPrincipal UserVo userVo,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){

        return ResponseEntity.ok(myPageFacade.getMyCreatedGroups(userVo.getId(), pageable));
    }

    @GetMapping("/my-posts/communities")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @Operation(
        summary = "내가 작성한 커뮤니티 게시글 목록을 조회합니다.",
        description = "마이페이지 > 나의활동 > '내 게시글' > '커뮤니티' 하위탭<br>" +
                     "내가 작성한 커뮤니티 게시글 목록을 최신순으로 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(
            schema = @Schema(implementation = Page.class),
            examples = @ExampleObject(value = """
                {
                    "content": [
                        {
                            "id": 1,
                            "thumbnailUrl": "https://example.com/image.jpg",
                            "categoryDisplayName": "정보공유",
                            "title": "내가 작성한 커뮤니티 게시글",
                            "contentPreview": "이것은 내가 작성한 게시글입니다...",
                            "createdAt": "2025-11-25T14:30:00",
                            "category": "INFO_SHARE"
                        },
                        {
                            "id": 2,
                            "thumbnailUrl": null,
                            "categoryDisplayName": "자유게시판",
                            "title": "또 다른 내 게시글",
                            "contentPreview": "자유게시판에 올린 글입니다...",
                            "createdAt": "2025-11-24T09:15:00",
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
                    "totalElements": 2,
                    "totalPages": 1,
                    "last": true,
                    "first": true,
                    "numberOfElements": 2
                }
                """)
    ))
    public ResponseEntity<Page<MyCommunityActivityResponse>> getMyPostCommunities(
            @AuthenticationPrincipal UserVo userVo,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){

        return ResponseEntity.ok(myPageFacade.getMyCreatedCommunities(userVo.getId(), pageable));
    }

    @GetMapping("/my-comments")
    @SecurityRequirement(name = "JWT Authentication")
    @Secured(UserRole.USER_TYPE)
    @Operation(
        summary = "내가 댓글 단 게시글 목록을 조회합니다.",
        description = "마이페이지 > 나의활동 > '댓글 7' 탭<br>" +
                     "내가 댓글을 작성한 커뮤니티 게시글 목록을 최신 댓글 작성순으로 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(
            schema = @Schema(implementation = Page.class),
            examples = @ExampleObject(value = """
                {
                    "content": [
                        {
                            "id": 1,
                            "thumbnailUrl": "https://example.com/image.jpg",
                            "categoryDisplayName": "정보공유",
                            "title": "내가 댓글 단 게시글",
                            "contentPreview": "이 게시글에 댓글을 남겼습니다...",
                            "createdAt": "2025-11-25T14:30:00",
                            "category": "INFO_SHARE"
                        },
                        {
                            "id": 2,
                            "thumbnailUrl": null,
                            "categoryDisplayName": "자유게시판",
                            "title": "또 다른 댓글 단 게시글",
                            "contentPreview": "여기에도 댓글을 달았네요...",
                            "createdAt": "2025-11-24T09:15:00",
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
                    "totalElements": 2,
                    "totalPages": 1,
                    "last": true,
                    "first": true,
                    "numberOfElements": 2
                }
                """)
    ))
    public ResponseEntity<Page<MyCommunityActivityResponse>> getCommentInfo(
            @AuthenticationPrincipal UserVo userVo,
            @PageableDefault(size = 20) Pageable pageable){

        // Sort를 제거한 Pageable 생성 (네이티브 쿼리에서 ORDER BY가 고정되어 있음)
        Pageable unsortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(myPageFacade.getMyCommentedCommunities(userVo.getId(), unsortedPageable));
    }
}
