package com.helpie.backend.repository.group;

import com.helpie.backend.domain.group.Bookmark;
import com.helpie.backend.domain.group.Group;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByUserIdAndGroupId(Long userId, Long groupId);

    Bookmark findByUserIdAndGroupId(Long userId, Long groupId);

    List<Bookmark> findAllByUserIdAndGroupIdIn(Long userId, List<Long> groupIds);

    /**
     * 사용자가 북마크한 소모임들 조회 (최신 북마크순, bookmarkYn이 true인 것만)
     */
    @Query("SELECT g FROM Bookmark b JOIN Group g ON b.groupId = g.id " +
           "WHERE b.userId = :userId AND b.bookmarkYn = true " +
           "ORDER BY b.createdAt DESC")
    Page<Group> findBookmarkedGroupsByUserId(@Param("userId") Long userId, Pageable pageable);
}
