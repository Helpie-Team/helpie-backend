package com.helpie.backend.group.respository;

import static org.assertj.core.api.Assertions.assertThat;

import com.helpie.backend.common.builder.BuilderSupporter;
import com.helpie.backend.common.builder.TestFixtureBuilder;
import com.helpie.backend.common.fixtures.AuthFixtures;
import com.helpie.backend.common.fixtures.GroupFixtures;
import com.helpie.backend.config.QueryDslConfig;
import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.group.GroupImage;
import com.helpie.backend.dto.group.GroupResponse;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value= {TestFixtureBuilder.class, BuilderSupporter.class, QueryDslConfig.class})
public class ImageRepositoryTest {

    @Autowired
    TestFixtureBuilder builder;

    @Autowired
    BuilderSupporter builderSupporter;

    private final Long userId=AuthFixtures.user().getId();
    private List<Group> groups = new ArrayList<>();


    @BeforeEach
    void setUp() {
        groups.add(builder.buildGroup(GroupFixtures.FIRST_CREATED_AT));
    }

    @Test
    @DisplayName("이미지가 여러 장 존재하면 가장 id가 작은 값을 반환한다")
    void multi_image_return_minimum_id(){
        int groupIdx=0;
        Group group = groups.get(groupIdx);
        GroupImage img1=builder.buildGroupImage(group,"test1");
        GroupImage img2=builder.buildGroupImage(group,"test2");

        List<GroupResponse> result=builderSupporter.groupRepository().findGroupsWithImagesByIds(List.of(group.getId()), userId);

        assertThat(result.get(groupIdx).getThumbnail()).isEqualTo(img1.getImageUrl());

    }

    @Test
    @DisplayName("이미지가 존재하지 않으면 null을 반환한다")
    void no_image_return_null(){
        int groupIdx=0;
        Group group = groups.get(groupIdx);

        List<GroupResponse> result=builderSupporter.groupRepository().findGroupsWithImagesByIds(List.of(group.getId()), userId);

        assertThat(result.get(groupIdx).getThumbnail()).isNull();
    }


}
