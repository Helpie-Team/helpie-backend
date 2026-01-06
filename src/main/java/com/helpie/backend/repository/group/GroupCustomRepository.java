package com.helpie.backend.repository.group;

import com.helpie.backend.domain.location.City;
import com.helpie.backend.dto.group.CursorRequest;
import com.helpie.backend.dto.group.GroupResponse;
import com.helpie.backend.dto.group.MyGroupResponse;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupCustomRepository {

    Page<MyGroupResponse> findMyGroups(Long userId, String status, Pageable pageable);

    List<Long> findPageIds(List<City> cities, CursorRequest request);

    List<GroupResponse> findGroupsWithImagesByIds(List<Long> ids,Long userId);
}
