package com.helpie.backend.common.fixtures;

import com.helpie.backend.domain.survey.Interest;
import com.helpie.backend.dto.group.GroupCreateResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class GroupFixtures {

    public static GroupCreateResponse CREATE_RESPONSE= new GroupCreateResponse(
            1L,
            "title",
            "description",
            10,
            "서울",
            Set.of(Interest.WALKING),
            List.of(),
            LocalDateTime.of(2026, 1, 1, 10, 0),
            100L
    );

    public static RequestPostProcessor validParams() {
        return request -> {
            request.addParameter("title", "title");
            request.addParameter("description", "description");
            request.addParameter("cityId", "1");
            request.addParameter("interests", "WALKING");
            request.addParameter("category", "HOBBY");
            request.addParameter("meetingDate", "2026-01-01T10:00:00");
            request.addParameter("maxMember", "10");
            return request;
        };
    }


}
