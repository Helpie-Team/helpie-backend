package com.helpie.backend.group;

import static org.assertj.core.api.Assertions.assertThat;

import com.helpie.backend.common.builder.BuilderSupporter;
import com.helpie.backend.common.builder.TestFixtureBuilder;
import com.helpie.backend.common.fixtures.AuthFixtures;
import com.helpie.backend.common.fixtures.GroupFixtures;

import com.helpie.backend.config.QueryDslConfig;
import com.helpie.backend.domain.group.Group;

import com.helpie.backend.domain.location.City;
import com.helpie.backend.dto.group.CursorRequest;
import com.helpie.backend.dto.group.GroupResponse;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value= {TestFixtureBuilder.class, BuilderSupporter.class, QueryDslConfig.class})
public class CursorRepositoryTest {

    @Autowired
    TestFixtureBuilder builder;

    @Autowired
    BuilderSupporter builderSupporter;

    @Test
    @DisplayName("첫 페이지에서 커서 파라미터를 전달하지 않는다")
    void createdAt_no_param() {
        Group g1 = builder.buildGroup(GroupFixtures.FIRST_CREATED_AT);
        Group g2 = builder.buildGroup(GroupFixtures.SECOND_CREATED_AT);
        Group g3 = builder.buildGroup(GroupFixtures.THIRD_CREATED_AT);
        City cities=builder.buildCity();
        CursorRequest request=GroupFixtures.cursorRequest(null,null,1);
        List<Long> ret=builderSupporter.groupRepository().findPageIds(List.of(cities),request);
        List<GroupResponse> result=builderSupporter.groupRepository().findGroupsWithImagesByIds(ret, AuthFixtures.user().getId());

        assertThat(result).extracting(GroupResponse::getId)
            .containsExactly(
                g3.getId(),
                g2.getId()
            );
    }

    @Test
    @DisplayName("createdAt 내림차순으로 반환한다")
    void createdAt_desc() {
        Group g1 = builder.buildGroup(GroupFixtures.FIRST_CREATED_AT);
        Group g2 = builder.buildGroup(GroupFixtures.SECOND_CREATED_AT);
        Group g3 = builder.buildGroup(GroupFixtures.THIRD_CREATED_AT);
        City cities=builder.buildCity();
        Long cursorId = g1.getId();
        int size=3;

        CursorRequest request=GroupFixtures.cursorRequest(cursorId,GroupFixtures.CURSOR_CREATED_AT,size);

        List<Long> ret=builderSupporter.groupRepository().findPageIds(List.of(cities),request);
        List<GroupResponse> result=builderSupporter.groupRepository().findGroupsWithImagesByIds(ret, AuthFixtures.user().getId());

        assertThat(result).extracting(GroupResponse::getId)
            .containsExactly(
                g3.getId(),
                g2.getId(),
                g1.getId()
            );
    }


    @Test
    @DisplayName("createdAt이 동일할 때 id를 내림차순으로 반환한다")
    void same_createdAt_id_desc() {
        int size = 5;
        City cities=builder.buildCity();
        CursorRequest request=GroupFixtures.cursorRequest(null,null,size);

        Group g1 = builder.buildGroup(GroupFixtures.FIRST_CREATED_AT);
        Group g2 =builder.buildGroup(GroupFixtures.FIRST_CREATED_AT);
        Group g3 =builder.buildGroup(GroupFixtures.FIRST_CREATED_AT);
        Group g4 = builder.buildGroup(GroupFixtures.FIRST_CREATED_AT);

        List<Long> ret=builderSupporter.groupRepository().findPageIds(List.of(cities),request);
        List<GroupResponse> result=builderSupporter.groupRepository().findGroupsWithImagesByIds(ret, AuthFixtures.user().getId());

        assertThat(result).extracting(GroupResponse::getId)
            .containsExactly(
                g4.getId(),
                g3.getId(),
                g2.getId(),
                g1.getId()
            );
    }

}
