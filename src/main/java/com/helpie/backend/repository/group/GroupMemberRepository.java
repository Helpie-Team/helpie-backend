package com.helpie.backend.repository.group;

import com.helpie.backend.domain.group.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 소모임 멤버 Repository
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    
    /**
     * 소모임별 활성 멤버 목록을 조회합니다.
     */
    List<GroupMember> findByGroupIdAndIsActive(Long groupId, Boolean isActive);
    
    /**
     * 사용자별 활성 참여 소모임 목록을 조회합니다.
     */
    List<GroupMember> findByUserIdAndIsActive(Long userId, Boolean isActive);
    
    /**
     * 특정 소모임의 특정 사용자 멤버 정보를 조회합니다.
     */
    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);
    
    /**
     * 소모임별 활성 멤버 수를 조회합니다.
     */
    Long countByGroupIdAndIsActive(Long groupId, Boolean isActive);
}