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
public interface GroupRepository extends JpaRepository<Group, Long>,GroupCustomRepository {
    
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
    JOIN FETCH g.city c
    JOIN FETCH c.country
    LEFT JOIN FETCH g.images
    WHERE g.city IN :cities
      AND (:category = 'ALL' OR g.category = :category)
      AND g.status IN ('RECRUITING', 'RECRUITMENT_CLOSED')
""")
    Page<Group> findAllByFilters(@Param("cities") List<City> cities, @Param("category") Category category, Pageable pageable);



    @Query(
        value = """
        SELECT *
        FROM user_groups
        WHERE status IN ('RECRUITING', 'RECRUITMENT_CLOSED')
        ORDER BY RAND(:seed)
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM user_groups
        WHERE status IN ('RECRUITING', 'RECRUITMENT_CLOSED')
        """,
        nativeQuery = true
    )
    Page<Group> findByInterestFiltersV2(@Param("seed") long seed, Pageable pageable);




    @Query("""
    SELECT g FROM Group g
      WHERE g.status IN ('RECRUITING', 'RECRUITMENT_CLOSED')
       AND EXISTS (
           SELECT i FROM g.interests i
            WHERE i IN :interests
          )

""")
    Page<Group> findByInterestFilters(@Param("city") City city, @Param("interests") Set<Interest> interests, Pageable pageable);


    @Query("""
    SELECT DISTINCT g
    FROM Group g
    JOIN FETCH g.city c
    JOIN FETCH c.country
    LEFT JOIN FETCH g.images
    WHERE g.city IN :cities
      AND g.status IN ('RECRUITING', 'RECRUITMENT_CLOSED')
      AND (
                    g.title LIKE CONCAT('%', :keyword, '%')
                    OR g.description LIKE CONCAT('%', :keyword, '%')
                )
""")

    Page<Group> findByKeyword(List<City> cities, String keyword, Pageable pageable);
}