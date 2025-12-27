package com.helpie.backend.repository.group;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.location.City;
import com.helpie.backend.dto.group.MyGroupResponse;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupCustomRepository {

    Page<MyGroupResponse> findMyGroups(Long userId, String status, Pageable pageable);

    List<Group> findPage(List<City> cities, Category category, LocalDateTime cursorCreatedAt, Long cursorId, int size);
}
