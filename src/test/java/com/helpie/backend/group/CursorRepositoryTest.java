package com.helpie.backend.group;

import static org.assertj.core.api.Assertions.assertThat;

import com.helpie.backend.common.builder.BuilderSupporter;
import com.helpie.backend.common.builder.TestFixtureBuilder;
import com.helpie.backend.common.fixtures.GroupFixtures;
import com.helpie.backend.config.QueryDslConfig;
import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.group.Group;
import com.helpie.backend.repository.group.impl.GroupCustomRepositoryImpl;

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
    GroupCustomRepositoryImpl groupCustomRepository;

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

        List<Group> result = groupCustomRepository.findPage(
            List.of(builder.buildCity()),
            Category.HOBBY,
            null,
            null,2
        );

        assertThat(result).extracting(Group::getId)
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

        Long cursorId = g1.getId();

        List<Group> result = builderSupporter.groupRepository().findPage(
            List.of(builder.buildCity()),
            Category.HOBBY,
            GroupFixtures.CURSOR_CREATED_AT,
            cursorId,2
        );

        assertThat(result).extracting(Group::getId)
            .containsExactly(
                g3.getId(),
                g2.getId()
            );
    }


    @Test
    @DisplayName("createdAt이 동일할 때 id를 내림차순으로 반환한다")
    void same_createdAt_id_desc() {
        Group g1 = builder.buildGroup(GroupFixtures.FIRST_CREATED_AT);
        Group g2 =builder.buildGroup(GroupFixtures.FIRST_CREATED_AT);
        Group g3 =builder.buildGroup(GroupFixtures.FIRST_CREATED_AT);
        Group g4 = builder.buildGroup(GroupFixtures.FIRST_CREATED_AT);


        Long cursorId = g3.getId();

        List<Group> result = groupCustomRepository.findPage(
            List.of(builder.buildCity()),
            Category.HOBBY,
            GroupFixtures.CURSOR_CREATED_AT,
            cursorId,
            5

        );

        assertThat(result).extracting(Group::getId)
            .containsExactly(
                g4.getId(),
                g3.getId(),
                g2.getId(),
                g1.getId()
            );
    }

}
