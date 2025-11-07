package com.helpie.backend.facade.mypage;

import com.helpie.backend.domain.group.GroupStatus;
import com.helpie.backend.dto.group.MyGroupResponse;
import com.helpie.backend.dto.mypage.response.MyProfileResponse;
import com.helpie.backend.service.group.GroupService;
import com.helpie.backend.service.location.LocationService;
import com.helpie.backend.service.survey.SurveyBasicInfoService;
import com.helpie.backend.service.user.UserCommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageFacade {
    private final UserCommonService userCommonService;
    private final SurveyBasicInfoService surveyBasicInfoService;
    private final LocationService locationService;
    private final GroupService groupService;

    public MyProfileResponse getMyProfileInfo(Long userId) {
        final var user = userCommonService.findById(userId);
        final var surveyBasicInfo = surveyBasicInfoService.getSurveyBasicInfo(user.getId());
        return new MyProfileResponse(
                user.getUsername(),
                user.getEmail(),
                surveyBasicInfo,
                locationService.getCityById(surveyBasicInfo.getCityId())
        );
    }

    public Page<MyGroupResponse> getMyGroups(Long userId, GroupStatus groupStatus, Pageable pageable) {
        return groupService.getMyGroups(userId, groupStatus, pageable);
    }
}
