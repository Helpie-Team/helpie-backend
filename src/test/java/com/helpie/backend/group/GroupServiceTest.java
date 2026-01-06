package com.helpie.backend.group;

import static org.assertj.core.api.Assertions.assertThat;

import com.helpie.backend.common.builder.TestFixtureBuilder;
import com.helpie.backend.common.fixtures.AuthFixtures;
import com.helpie.backend.common.fixtures.GroupFixtures;
import com.helpie.backend.domain.group.Group;
import com.helpie.backend.dto.group.CursorRequest;
import com.helpie.backend.dto.group.GroupResponse;

import com.helpie.backend.service.group.GroupService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class GroupServiceTest {

    @Autowired
    private TestFixtureBuilder builder;

    @Autowired
    private GroupService groupService;

    private List<Group> groups = new ArrayList<>();

    @BeforeEach
    void setUp() {
        groups.add(builder.buildGroup(GroupFixtures.FIRST_CREATED_AT));
        groups.add(builder.buildGroup(GroupFixtures.SECOND_CREATED_AT));
        groups.add(builder.buildGroup(GroupFixtures.THIRD_CREATED_AT));
    }

    @Test
    @DisplayName("다음 페이지를 조회할 때 이전 페이지와 중복되지 않는다")
    void no_duplicated_content() {
        int size = groups.size() - 1;

        CursorRequest request = GroupFixtures.cursorRequest(null, null, size);
        var firstResponse = groupService.getGroups(
            AuthFixtures.user().getId(),
            request
        );

        assertThat(firstResponse.hasNext()).isTrue();

        var nextCursorId = firstResponse.nextCursor().id();
        var nextCreatedAt = firstResponse.nextCursor().createdAt();

        CursorRequest nextRequest = GroupFixtures.cursorRequest(nextCursorId, nextCreatedAt, size);
        var secondResponse = groupService.getGroups(
            AuthFixtures.user().getId(),
            nextRequest
        );

        Set<Long> set1 = new HashSet<>(
            firstResponse.content().stream().map(GroupResponse::getId).collect(Collectors.toSet()));
        Set<Long> set2 = new HashSet<>(secondResponse.content().stream().map(GroupResponse::getId)
            .collect(Collectors.toSet()));

        assertThat(set1).doesNotContainAnyElementsOf(set2);
    }

    @Test
    @DisplayName("마지막 페이지를 요청할 때 커서가 존재하지 않는다")
    void last_page_hasNext_false() {
        // given
        Integer size = groups.size();
        Group last=groups.get(size-1);
        Long cursorId = last.getId();
        LocalDateTime createdAt = last.getCreatedAt();
        Long userId= AuthFixtures.user().getId();

        CursorRequest request = GroupFixtures.cursorRequest(cursorId, createdAt, size);
        var response = groupService.getGroups(
            userId,
            request
        );

        assertThat(response.nextCursor()).isNull();
        assertThat(response.hasNext()).isFalse();

    }

}
