package com.helpie.backend.dto.group;

import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.survey.Interest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record GroupCreateResponse(
    Long id,
    String title,
    String description,
    Integer maxMember,
    String city,
    Set<Interest> interest,
    List<String> imageUrls,
    LocalDateTime meetingDate,
    Long chatRoomId
) {

    public static GroupCreateResponse from(
        Group group,
        List<String> imageUrls,
        Long chatRoomId
    ) {
        return new GroupCreateResponse(
            group.getId(),
            group.getTitle(),
            group.getDescription(),
            group.getMaxMembers(),
            group.getCity().getName(),
            group.getInterests(),
            imageUrls,
            group.getMeetingDate(),
            chatRoomId
        );
    }

}
