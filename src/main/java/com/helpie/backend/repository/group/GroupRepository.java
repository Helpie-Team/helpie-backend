package com.helpie.backend.repository.group;

import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.group.GroupStatus;
import com.helpie.backend.domain.survey.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 소모임 Repository
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
public interface GroupRepository extends JpaRepository<Group, Long> {
    
    /**
     * 나라별, 상태별 소모임 목록을 조회합니다.
     */
    List<Group> findByCountryAndStatus(Country country, GroupStatus status);
    
    /**
     * 활성 상태인 소모임 목록을 조회합니다.
     */
    List<Group> findByStatus(GroupStatus status);
}