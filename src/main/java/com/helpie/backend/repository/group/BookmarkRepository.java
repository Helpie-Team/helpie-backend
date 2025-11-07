package com.helpie.backend.repository.group;

import com.helpie.backend.domain.group.Bookmark;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByUserIdAndGroupId(Long userId, Long groupId);

    Bookmark findByUserIdAndGroupId(Long userId, Long groupId);

    List<Bookmark> findAllByUserIdAndGroupIdIn(Long userId, List<Long> groupIds);
}
