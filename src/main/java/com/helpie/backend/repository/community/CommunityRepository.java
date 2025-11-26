package com.helpie.backend.repository.community;

import com.helpie.backend.domain.community.Community;
import com.helpie.backend.domain.community.CommunityCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 커뮤니티 Repository
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    
    /**
     * 카테고리별 게시글 조회 (페이징)
     */
    Page<Community> findByCategoryOrderByCreatedAtDesc(CommunityCategory category, Pageable pageable);
    
    /**
     * 전체 게시글 조회 (페이징)
     */
    Page<Community> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 사용자별 게시글 조회
     */
    @Query("SELECT c FROM Community c LEFT JOIN FETCH c.images WHERE c.userId = :userId ORDER BY c.createdAt DESC")
    Page<Community> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 제목으로 검색
     */
    Page<Community> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title, Pageable pageable);
    
    /**
     * 내용으로 검색
     */
    Page<Community> findByContentContainingIgnoreCaseOrderByCreatedAtDesc(String content, Pageable pageable);
    
    /**
     * 제목 또는 내용으로 검색
     */
    @Query("SELECT c FROM Community c WHERE " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY c.createdAt DESC")
    Page<Community> findByTitleOrContentContaining(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 카테고리별 제목 또는 내용으로 검색
     */
    @Query("SELECT c FROM Community c WHERE " +
           "c.category = :category AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY c.createdAt DESC")
    Page<Community> findByCategoryAndTitleOrContentContaining(
        @Param("category") CommunityCategory category, 
        @Param("keyword") String keyword, 
        Pageable pageable
    );
    
    /**
     * 조회수 증가
     */
    @Modifying
    @Query("UPDATE Community c SET c.viewCount = c.viewCount + 1 WHERE c.id = :id")
    void incrementViewCount(@Param("id") Long id);
    
    /**
     * 실시간 인기글 조회 (조회수 기준 상위 5개)
     */
    List<Community> findTop5ByOrderByViewCountDescCreatedAtDesc();
    
    /**
     * 추천글 조회 (좋아요 수 기준 상위 5개)
     */
    @Query("SELECT c FROM Community c LEFT JOIN c.likes l " +
           "GROUP BY c.id " +
           "ORDER BY COUNT(l.id) DESC, c.createdAt DESC")
    List<Community> findTop5ByLikesCount(Pageable pageable);
    
    /**
     * 사용자가 작성한 게시글들이 받은 총 좋아요 수
     */
    @Query("SELECT COALESCE(COUNT(l.id), 0) FROM Community c LEFT JOIN c.likes l WHERE c.userId = :userId")
    Integer countTotalLikesByUserId(@Param("userId") Long userId);
    
    /**
     * 사용자가 작성한 게시글들이 받은 총 댓글 수  
     */
    @Query("SELECT COALESCE(COUNT(com.id), 0) FROM Community c LEFT JOIN c.comments com WHERE c.userId = :userId")
    Integer countTotalCommentsByUserId(@Param("userId") Long userId);
    
    /**
     * 사용자가 작성한 게시글 수
     */
    Integer countByUserId(Long userId);
    
}