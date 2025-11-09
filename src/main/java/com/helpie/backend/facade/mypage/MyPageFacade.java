package com.helpie.backend.facade.mypage;

import com.helpie.backend.domain.group.GroupStatus;
import com.helpie.backend.domain.survey.SurveyBasicInfo;
import com.helpie.backend.domain.user.UserImage;
import com.helpie.backend.dto.group.GroupResponse;
import com.helpie.backend.dto.group.MyGroupResponse;
import com.helpie.backend.dto.mypage.response.MyProfileResponse;
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

    public Page<MyGroupResponse> getMyGroups(Long userId, GroupStatus groupStatus, Pageable pageable) {
        return groupService.getMyGroups(userId, groupStatus, pageable);
    }

    public Page<GroupResponse> getMyBookmarks(Long userId, Pageable pageable) {
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
}
