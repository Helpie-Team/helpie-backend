package com.helpie.backend.service.group;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.group.Bookmark;
import com.helpie.backend.domain.group.GroupImage;
import com.helpie.backend.domain.group.GroupMember;
import com.helpie.backend.domain.location.City;
import com.helpie.backend.domain.location.Country;
import com.helpie.backend.domain.survey.SurveyBasicInfo;
import com.helpie.backend.dto.group.*;
import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.ErrorCode;
import com.helpie.backend.exception.GroupException;
import com.helpie.backend.repository.chatroom.ChatRoomRepository;
import com.helpie.backend.repository.group.BookmarkRepository;
import com.helpie.backend.repository.group.GroupMemberRepository;
import com.helpie.backend.repository.group.GroupRepository;
import com.helpie.backend.repository.location.CityRepository;
import com.helpie.backend.repository.location.CountryRepository;
import com.helpie.backend.repository.survey.SurveyBasicInfoRepository;
import com.helpie.backend.service.chatroom.ChatRoomService;

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
    private final BookmarkRepository bookmarkRepository;
    private final ChatRoomRepository chatRoomRepository;


    @Transactional
    public GroupCreateResponse createGroup(Long userId, GroupCreateRequest request,List<String> urls,City city) {
        Group group = groupRepository.save(request.toEntity(city, userId));
        group.addMember(userId);

        if (!urls.isEmpty()) {
            for (String url : urls) {
                GroupImage image = new GroupImage(group, url);
                group.addImage(image);
            }
        }

        groupMemberRepository.save(new GroupMember(group, userId));

        String welcomeMessage = String.format("🎉 %s 소모임이 시작되었습니다! 즐거운 모임 되세요!", group.getTitle());
        Long chatRoomId = chatRoomService.createChatRoomAndJoin(group, userId, null, welcomeMessage);

        return GroupCreateResponse.from(group,urls,chatRoomId);
    }

    /**
     * 소모임에 가입하고 채팅방에 자동 입장합니다.
     */
    @Transactional
    public JoinResponse joinGroup(Long groupId, Long userId, String userName) {
        log.debug("소모임 가입 시작 - groupId: {}, userId: {}", groupId, userId);

        // 1. 소모임 조회 및 검증
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소모임입니다: " + groupId));

        // 2. 중복 가입 검증
        Optional<GroupMember> existingMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (existingMember.isPresent() && existingMember.get().getIsActive()) {
            throw new IllegalStateException("이미 가입된 소모임입니다: " + groupId);
        }

        //재가입 추가
        if (existingMember.isPresent()) {
            existingMember.get().rejoin();
        }else{
            // 3. 소모임 멤버 추가
            group.addMember(userId); // 현재 인원수 증가
            GroupMember groupMember = new GroupMember(group, userId, userName, true);
            groupMemberRepository.save(groupMember);
            log.info("소모임 가입 완료 - groupId: {}, userId: {}", groupId, userId);

            // 4. 채팅방에 자동 입장 (ChatRoomService에 위임)
            String joinMessage = String.format("👋 %s님이 소모임에 참가하셨습니다! 환영해주세요!",
                userName != null ? userName : "새로운 멤버");
            Long chatroomId=chatRoomService.autoJoinGroupChatRoom(groupId, userId, userName, joinMessage);

            log.info("소모임 가입 및 채팅방 자동 입장 완료 - groupId: {}, userId: {}", groupId, userId);
        }

        return new JoinResponse(chatRoomRepository.findByGroupId(groupId).get().getId(),"소모임 가입이 완료되었습니다");
    }
    @Transactional

    public JoinResponse enterChatRoom(Long roomId, Long id, String username) {
        chatRoomService.enterChatRoom(roomId, id, username);

        return new JoinResponse(roomId,"채팅방 입장이 완료되었습니다");
    }

    /**
     * groupId 별 조회
     */
    public GroupResponse getGroupById(Long groupId) {
        Group group=groupRepository.findById(groupId).orElseThrow(()->new BusinessException(ErrorCode.NO_GROUP_INFO) {
        });

        return GroupResponse.from(group);
    }

    public CursorResponse<GroupResponse> getGroups(Long userId,CursorRequest request) {
        List<City> cities= validateCountry(request.getCountry());
        List<Long> fetchedIds = groupRepository.findPageIds(cities,request);

        boolean hasNext = fetchedIds.size() > request.getSize();
        List<Long> pageIds = hasNext ? fetchedIds.subList(0, request.getSize()) : fetchedIds;
        List<GroupResponse> content=groupRepository.findGroupsWithImagesByIds(pageIds,userId);

        if (!hasNext){
            return new CursorResponse<>(content, hasNext, null);
        }

        GroupResponse last = content.get(content.size() - 1);
        CursorResponse.NextCursor  nextCursor = new CursorResponse.NextCursor(last.getCreatedAt(), last.getId());

        return new CursorResponse<>(content, hasNext, nextCursor);
    }

    private List<City> validateCountry(String countryCode) {
        if (countryCode.equals("ALL")){
            return cityRepository.findAll();
        }

        Country country = countryRepository.findByCode(countryCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 국가 코드입니다."));

        return cityRepository.findAllByCountry(country);

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


    public RecommendedResponse getGroupsByInterestV2(Long userId, Pageable pageable) {
        Optional<SurveyBasicInfo> surveyInfo = surveyBasicInfoRepository.findByUserId(userId);

        if (surveyInfo.isEmpty()) {
            return RecommendedResponse.locked("SURVEY_REQUIRED", pageable);
        }

        Page<Group> groups=groupRepository.findByInterestFiltersV2(userId,pageable);


        Page<GroupResponse> responsePage = mapGroupsWithBookmarks(userId, groups);
        return RecommendedResponse.ok(responsePage);
    }

    /**
      상태별 나의 소모임 조회
     // TODO: 지난 모임에 대한 isActive false 처리 필요
     */
    public Page<MyGroupResponse> getMyGroups(Long userId, String status, Pageable pageable) {
        return groupRepository.findMyGroups(userId, status, pageable);
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
        Country country = countryRepository.findByCode(code).orElseThrow(()-> new GroupException(ErrorCode.INTERNAL_SERVER_ERROR,"존재하지 않는 국가입니다"));

        return groupRepository
            .findAllByFilters(country.getCities(),category,pageable)
            .map(GroupResponse::from);

    }

    /**
     전체 국가 소모임 조회
     */
    public Page<GroupResponse> browseAllCountry(Category category, Pageable pageable) {
        List<City> cities=cityRepository.findAll();

        return groupRepository
            .findAllByFilters(cities,category,pageable)
            .map(GroupResponse::from);

    }

    /**
     로그인: 검색어로 조회
     */
    public Page<GroupResponse> getKeywordByUserV2(Long userId,String code, String keyword, Pageable pageable) {
        List<City> cities=getByCode(code);

        Page<Group> group=groupRepository.findByKeyword(cities, keyword, pageable);
        return mapGroupsWithBookmarks(userId, group);
    }

    public Page<GroupResponse> getByKeywordV2(String code, String keyword, Pageable pageable) {
        log.info("keyword: {}, code: {}", keyword,code);
        List<City> cities=getByCode(code);
        return groupRepository.findByKeyword(cities, keyword, pageable).map(GroupResponse::from);

    }

    private List<City> getByCode(String code){
        if (code.equals("ALL")){
            return cityRepository.findAll();
        }
        else{
            return countryRepository.findByCode(code)
                .orElseThrow(()->new GroupException(ErrorCode.INTERNAL_SERVER_ERROR,"존재하지 않는 국가입니다"))
                .getCities();
        }
    }

    @Transactional
    public void cancelGroup(Long userId, long groupId) {
        final var groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (groupMember.isEmpty()) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "User is not a member of the group") {
            };
        }

        if (!groupMember.get().getIsActive()) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "이미 취소 된 모임 입니다.") {
            };
        }
        Group group=groupMember.get().getGroup();
        group.removeMember(userId);
        groupMember.get().leave();
    }


    public JoinStateResponse getJoinState(Long userId, Long groupId) {
        Optional<GroupMember> groupMember=groupMemberRepository.findByGroupIdAndUserId(groupId,userId);

        if (groupMember.isEmpty() ||!groupMember.get().getIsActive()) {
            return new JoinStateResponse(false);
        }

        return new JoinStateResponse(true);
    }
}
