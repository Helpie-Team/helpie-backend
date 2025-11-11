package com.helpie.backend.repository.group;

import com.helpie.backend.domain.group.GroupStatus;
import com.helpie.backend.dto.group.MyGroupResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupCustomRepository {

    Page<MyGroupResponse> findMyGroups(Long userId, String status, Pageable pageable);
}
