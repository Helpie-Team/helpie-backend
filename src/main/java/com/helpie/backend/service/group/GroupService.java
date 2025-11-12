package com.helpie.backend.service.group;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.group.Bookmark;
import com.helpie.backend.domain.group.GroupImage;
import com.helpie.backend.domain.group.GroupMember;
import com.helpie.backend.domain.location.City;
import com.helpie.backend.domain.survey.SurveyBasicInfo;
import com.helpie.backend.dto.group.*;
import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.ErrorCode;
import com.helpie.backend.repository.group.BookmarkRepository;
import com.helpie.backend.repository.group.GroupCustomRepository;
import com.helpie.backend.repository.group.GroupMemberRepository;
import com.helpie.backend.repository.group.GroupRepository;
import com.helpie.backend.repository.location.CityRepository;
import com.helpie.backend.repository.location.CountryRepository;
import com.helpie.backend.repository.survey.SurveyBasicInfoRepository;
import com.helpie.backend.service.chatroom.ChatRoomService;
import com.helpie.backend.service.file.FileService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
    private final FileService fileService;
    private final GroupCustomRepository groupCustomRepository;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public GroupCreateResponse createGroup(Long userId, GroupCreateRequest req, List<MultipartFile> images) {
        log.debug("소모임 생성 시작 - userId: {}, title: {}", userId, req.title());

        // 1. 도시 조회
        City city = cityRepository.findById(req.cityId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도시입니다: " + req.cityId()));

        Group group=Group.builder()
            .title(req.title())
            .description(req.description())
            .city(city)
            .category(req.category())
            .interests(req.interests())
            .maxMembers(req.maxMember())
            .meetingDate(req.meetingDate())
            .createdBy(userId)
            .build();

        Group savedGroup = groupRepository.save(group);
        log.info("소모임 생성 완료 - groupId: {}", savedGroup.getId());

        //TODO: 비동기처리
        List<String> urls;
        try {
            urls = fileService.uploadFiles(images);
                for (String url : urls) {
                    GroupImage image = new GroupImage(savedGroup, url);
                    savedGroup.addImage(image);
                }

        } catch (RuntimeException e) {
            log.warn("이미지를 업로드하지 않았습니다");
            urls = new ArrayList<>();
        }

        // 2. 소모임 멤버 추가
        GroupMember groupMember = new GroupMember(savedGroup, userId);
        groupMemberRepository.save(groupMember);
        log.info("소모임 멤버 추가 완료 - groupId: {}, userId: {}", savedGroup.getId(), userId);

        // 3. 채팅방 자동 생성 및 생성자 입장 (ChatRoomService에 위임)
        String welcomeMessage = String.format("🎉 %s 소모임이 시작되었습니다! 즐거운 모임 되세요!", savedGroup.getTitle());
        Long chatRoomId = chatRoomService.createChatRoomAndJoin(savedGroup, userId, null, welcomeMessage);


        log.info("소모임 생성 및 채팅방 자동 설정 완료 - groupId: {}, chatRoomId: {}",
            savedGroup.getId(), chatRoomId);

        return new GroupCreateResponse(
            savedGroup.getId(),
            savedGroup.getTitle(),
            savedGroup.getDescription(),
            savedGroup.getMaxMembers(),
            savedGroup.getCity().getName(),
            savedGroup.getInterests(),
            urls,
            savedGroup.getMeetingDate()
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
     * groupId 별 조회
     */
    public GroupResponse getGroupById(Long groupId) {
        Group group=groupRepository.findById(groupId).orElseThrow(()->new BusinessException(ErrorCode.NO_GROUP_INFO) {
        });

        return GroupResponse.from(group);
    }

    /**
     * 국가,카테고리별 소모임 조회
     */
    public Page<GroupResponse> getGroupByCountry(Long userId, String code, Category category, Pageable pageable) {
        List<City> cities = countryRepository.findByCode(code)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 국가 코드입니다."))
            .getCities();

        Page<Group> groups = groupRepository.findAllByFilters(cities, category, pageable);

        return mapGroupsWithBookmarks(userId, groups);
    }

    /**
     * 맞춤형: 도시, 흥미별 소모임 조회
     */
    public RecommendedResponse getGroupsByInterest(Long userId, Pageable pageable) {
        Optional<SurveyBasicInfo> surveyInfo = surveyBasicInfoRepository.findByUserId(userId);

        if (surveyInfo.isEmpty()) {
            return RecommendedResponse.locked("SURVEY_REQUIRED", pageable);
        }

        SurveyBasicInfo surveyBasicInfo = surveyInfo.get();
        Page<Group> groups = groupRepository.findByInterestFilters(
            surveyBasicInfo.getCity(), surveyBasicInfo.getInterests(), pageable
        );

        Page<GroupResponse> responsePage = mapGroupsWithBookmarks(userId, groups);
        return RecommendedResponse.ok(responsePage);
    }

    /**
      상태별 나의 소모임 조회
     // TODO: 지난 모임에 대한 isActive false 처리 필요
     */
    public Page<MyGroupResponse> getMyGroups(Long userId, String status, Pageable pageable) {
        return groupCustomRepository.findMyGroups(userId, status, pageable);
    }


    private Page<GroupResponse> mapGroupsWithBookmarks(Long userId, Page<Group> groups) {
        List<Long> groupIds = groups.stream()
            .map(Group::getId)
            .toList();

        Set<Long> bookmarkedIds = bookmarkRepository
            .findAllByUserIdAndGroupIdIn(userId, groupIds)
            .stream()
            .map(Bookmark::getGroupId)
            .collect(Collectors.toSet());

        return groups.map(group ->
            GroupResponse.from(group, bookmarkedIds.contains(group.getId()))
        );
    }

    public Page<GroupResponse> browseByCountry(String code, Category category, Pageable pageable) {
        List<City> cities=countryRepository.findByCode(code).get().getCities();

        return groupRepository
            .findAllByFilters(cities,category,pageable)
            .map(GroupResponse::from);

    }

    @Transactional
    public void cancelGroup(Long userId, long groupId) {
        final var groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (groupMember.isEmpty()) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "User is not a member of the group") {
            };
        }
        groupMember.get().leave();
    }
}
