package com.helpie.backend.service.group;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.group.GroupMember;
import com.helpie.backend.domain.group.GroupStatus;
import com.helpie.backend.domain.location.City;
import com.helpie.backend.domain.survey.SurveyBasicInfo;
import com.helpie.backend.dto.group.GroupCreateRequest;
import com.helpie.backend.dto.group.GroupCreateResponse;
import com.helpie.backend.dto.group.GroupResponse;
import com.helpie.backend.dto.group.RecommendedResponse;
import com.helpie.backend.repository.group.GroupMemberRepository;
import com.helpie.backend.repository.group.GroupRepository;
import com.helpie.backend.repository.location.CityRepository;
import com.helpie.backend.repository.location.CountryRepository;
import com.helpie.backend.repository.survey.SurveyBasicInfoRepository;
import com.helpie.backend.service.chatroom.ChatRoomService;
import com.helpie.backend.utils.storage.ImageStorage;
import java.util.List;
import java.util.Optional;
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
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final SurveyBasicInfoRepository surveyBasicInfoRepository;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final ChatRoomService chatRoomService;
    private final ImageStorage imageStorage;

    @Transactional
    public GroupCreateResponse createGroup(Long userId, GroupCreateRequest req, List<MultipartFile> images) {
        log.debug("소모임 생성 시작 - userId: {}, title: {}", userId, req.title());

        // 1. 도시 조회
        City city = cityRepository.findById(req.cityId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도시입니다: " + req.cityId()));
        
        // 2. 소모임 생성
        Group group = new Group(
            req.title(),
            req.description(),
            city,
            req.category(),
            req.interests(),
            req.maxMember(),
            req.endAt(),
            req.meetingDate()
            );
        Group savedGroup = groupRepository.save(group);
        log.info("소모임 생성 완료 - groupId: {}", savedGroup.getId());

        // 2. 소모임 멤버 추가
        GroupMember groupMember = new GroupMember(savedGroup, userId);
        groupMemberRepository.save(groupMember);
        log.info("소모임 멤버 추가 완료 - groupId: {}, userId: {}", savedGroup.getId(), userId);

        // 3. 채팅방 자동 생성 및 생성자 입장 (ChatRoomService에 위임)
        String welcomeMessage = String.format("🎉 %s 소모임이 시작되었습니다! 즐거운 모임 되세요!", savedGroup.getTitle());
        Long chatRoomId = chatRoomService.createChatRoomAndJoin(savedGroup, userId, null, welcomeMessage);

        // 4. 이미지 저장
        List<String> urls = imageStorage.storeAll(images, savedGroup);

        log.info("소모임 생성 및 채팅방 자동 설정 완료 - groupId: {}, chatRoomId: {}",
            savedGroup.getId(), chatRoomId);

        return new GroupCreateResponse(
            savedGroup.getId(),
            savedGroup.getTitle(),
            savedGroup.getDescription(),
            savedGroup.getMaxMembers(),
            savedGroup.getCity().getName(),
            savedGroup.getInterests(),
            urls
        );
    }

    /**
     * 소모임에 가입하고 채팅방에 자동 입장합니다.
     */
    @Transactional
    public void joinGroup(Long groupId, Long userId, String userName) {
        log.debug("소모임 가입 시작 - groupId: {}, userId: {}", groupId, userId);

        // 1. 소모임 조회 및 검증
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소모임입니다: " + groupId));

        // 2. 중복 가입 검증
        Optional<GroupMember> existingMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (existingMember.isPresent() && existingMember.get().getIsActive()) {
            throw new IllegalStateException("이미 가입된 소모임입니다: " + groupId);
        }

        // 3. 소모임 멤버 추가
        group.addMember(userId); // 현재 인원수 증가
        GroupMember groupMember = new GroupMember(group, userId, userName, true);
        groupMemberRepository.save(groupMember);
        log.info("소모임 가입 완료 - groupId: {}, userId: {}", groupId, userId);

        // 4. 채팅방에 자동 입장 (ChatRoomService에 위임)
        String joinMessage = String.format("👋 %s님이 소모임에 참가하셨습니다! 환영해주세요!",
            userName != null ? userName : "새로운 멤버");
        chatRoomService.autoJoinGroupChatRoom(groupId, userId, userName, joinMessage);

        log.info("소모임 가입 및 채팅방 자동 입장 완료 - groupId: {}, userId: {}", groupId, userId);
    }

    /**
     *
     * 로그인, 비로그인 공통
     나라와 카테고리별 소모임 조회
     */
    public Page<GroupResponse> getGroupByCountry(String code, Category category,Pageable pageable) {
        List<City> cities=countryRepository.findByCode(code).get().getCities();

        return groupRepository
            .findAllByFilters(cities,category,pageable)
            .map(GroupResponse::from);
    }


    /**
     맞춤형: 도시, 흥미별 소모임 조회
     */
    public RecommendedResponse getGroupsByInterest(Long userId, Pageable pageable) {
        Optional<SurveyBasicInfo> surveyInfo = surveyBasicInfoRepository.findByUserId(userId);

        if (surveyInfo.isEmpty()) {
            return RecommendedResponse.locked("SURVEY_REQUIRED", pageable);
        }
        SurveyBasicInfo surveyBasicInfo = surveyInfo.get();

        Page<GroupResponse> page = groupRepository
            .findByInterestFilters(surveyBasicInfo.getCity(),surveyBasicInfo.getInterests(), pageable)
            .map(GroupResponse::from);

        return RecommendedResponse.ok(page);

    }

}
