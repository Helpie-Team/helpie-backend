package com.helpie.backend.facade.mypage;

import com.helpie.backend.domain.group.GroupStatus;
import com.helpie.backend.domain.survey.SurveyBasicInfo;
import com.helpie.backend.domain.user.UserImage;
import com.helpie.backend.dto.group.GroupResponse;
import com.helpie.backend.dto.group.MyGroupResponse;
import com.helpie.backend.dto.mypage.response.MyBookmarkResponse;
import com.helpie.backend.dto.mypage.response.MyProfileResponse;
import com.helpie.backend.dto.community.MyCommunityResponse;
import com.helpie.backend.dto.mypage.response.MyCommunityActivityResponse;
import com.helpie.backend.dto.survey.SurveyBasicInfoResponse;
import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.service.file.FileService;
import com.helpie.backend.service.group.BookmarkService;
import com.helpie.backend.service.group.GroupService;
import com.helpie.backend.service.location.LocationService;
import com.helpie.backend.service.survey.SurveyBasicInfoService;
import com.helpie.backend.service.user.UserCommonService;
import com.helpie.backend.service.user.UserImageService;
import com.helpie.backend.service.user.UserService;
import com.helpie.backend.service.community.CommunityService;
import com.helpie.backend.domain.community.Community;
import com.helpie.backend.repository.community.CommunityRepository;
import com.helpie.backend.dto.community.CommunityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageFacade {
    private final UserCommonService userCommonService;
    private final SurveyBasicInfoService surveyBasicInfoService;
    private final LocationService locationService;
    private final GroupService groupService;
    private final FileService fileService;
    private final UserImageService userImageService;
    private final UserService userService;
    private final BookmarkService bookmarkService;
    private final CommunityService communityService;
    private final CommunityRepository communityRepository;

    public MyProfileResponse getMyProfileInfo(Long userId) {
        final var user = userCommonService.findById(userId);

        SurveyBasicInfoResponse surveyBasicInfo = null;

        try {
            surveyBasicInfo = surveyBasicInfoService.getSurveyBasicInfo(user.getId()); // 기존 throw 메서드 그대로 사용
        } catch (BusinessException e) {
            // 로그만 남기고 null 허용
            log.info("설문 기본정보 없음 - userId={}", userId);
        }

        final var userImage = userImageService.getUserImage(user.getId());

        return new MyProfileResponse(
                user.getUsername(),
                user.getEmail(),
                user.getSurveyStatus(),
                userImage.map(UserImage::getImageUrl).orElse(null),
                surveyBasicInfo,
                surveyBasicInfo == null ? null : locationService.getCityById(surveyBasicInfo.getCityId())
        );
    }

    public Page<MyGroupResponse> getMyGroups(Long userId, String status, Pageable pageable) {
        return groupService.getMyGroups(userId, status, pageable);
    }

    public Page<MyBookmarkResponse> getMyBookmarks(Long userId, Pageable pageable) {
        return bookmarkService.getByPreference(userId, pageable);
    }

    public void updateProfileImage(Long userId, MultipartFile profileImageFile) {
        String profileImageUrl = fileService.uploadFile(profileImageFile);
        userImageService.saveUserImage(userId, profileImageUrl);
    }


    public void resetProfileImage(Long userId) {
        userImageService.resetUserImage(userId);
    }

    public void updateProfileUsername(Long userId, String username) {
        userService.updateUsername(userId, username);
    }

    /**
     * 내 커뮤니티 정보 조회 (통계 + 활동 내역 통합)
     */
    public MyCommunityResponse getMyCommunities(Long userId, String sort, Pageable pageable) {
        // 통계 정보 계산
        Integer likeCount = getCommunityLikesCount(userId);
        Integer commentCount = getCommunityCommentsCount(userId);
        Integer postCount = getCommunityPostCount(userId);
        
        // 활동 내역 조회
        Page<MyCommunityActivityResponse> activities = communityService.getMyCommunities(userId, pageable)
            .map(communityResponse -> {
                String thumbnailUrl = extractThumbnailUrl(communityResponse);
                
                return new MyCommunityActivityResponse(
                    communityResponse.getId(),
                    thumbnailUrl,
                    communityResponse.getCategoryDisplayName(),
                    communityResponse.getTitle(),
                    communityResponse.getContent().length() > 100 
                        ? communityResponse.getContent().substring(0, 100) + "..."
                        : communityResponse.getContent(),
                    communityResponse.getCreatedAt(),
                    communityResponse.getCategory()
                );
            });
            
        return MyCommunityResponse.of(likeCount, commentCount, postCount, activities);
    }

    /**
     * 썸네일 URL 추출
     */
    private String extractThumbnailUrl(CommunityResponse communityResponse) {
        // CommunityResponse에서 첫 번째 이미지를 썸네일로 사용
        if (communityResponse.getImageUrls() != null && !communityResponse.getImageUrls().isEmpty()) {
            return communityResponse.getImageUrls().get(0);
        }
        // 이미지가 없으면 기본 썸네일 반환
        return "/api/v1/images/community-default-thumbnail.png";
    }

    /**
     * 내가 받은 좋아요 수 계산
     */
    private Integer getCommunityLikesCount(Long userId) {
        return communityRepository.countTotalLikesByUserId(userId);
    }

    /**
     * 내가 받은 댓글 수 계산
     */
    private Integer getCommunityCommentsCount(Long userId) {
        return communityRepository.countTotalCommentsByUserId(userId);
    }

    /**
     * 내가 쓴 글 수 계산
     */
    private Integer getCommunityPostCount(Long userId) {
        return communityRepository.countByUserId(userId);
    }
}
