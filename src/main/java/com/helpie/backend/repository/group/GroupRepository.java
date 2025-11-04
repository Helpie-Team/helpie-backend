package com.helpie.backend.repository.group;

import com.helpie.backend.domain.group.Category;
import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.group.GroupStatus;
import com.helpie.backend.domain.location.City;
import com.helpie.backend.domain.survey.Interest;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    List<Group> findByCityAndStatus(City city, GroupStatus status);
    
    /**
     * 활성 상태인 소모임 목록을 조회합니다.
     */
    List<Group> findByStatus(GroupStatus status);

    @Query("""
    SELECT g FROM Group g
    WHERE g.city IN :cities
      AND (:category = 'ALL' OR g.category = :category)
      AND g.status ='RECRUITING'
""")
    Page<Group> findAllByFilters(@Param("cities") List<City> cities, @Param("category") Category category, Pageable pageable);


    @Query("""
    SELECT g FROM Group g
    WHERE g.city = :city
      AND g.status ='RECRUITING'
       AND EXISTS (
           SELECT i FROM g.interests i
            WHERE i IN :interests
          )

""")
    Page<Group> findByInterestFilters(@Param("city") City city, @Param("interests") Set<Interest> interests, Pageable pageable);
}