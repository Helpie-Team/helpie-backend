package com.helpie.backend.repository.bookmark;

import com.helpie.backend.domain.group.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkCustomRepository {
    Page<Group> findLikeGroups(Long userId, Pageable pageable);

}
