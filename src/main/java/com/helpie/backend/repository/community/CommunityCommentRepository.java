package com.helpie.backend.repository.community;

import com.helpie.backend.domain.community.Community;
import com.helpie.backend.domain.community.CommunityComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 커뮤니티 댓글 리포지토리
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    
    /**
     * 특정 커뮤니티 게시글의 댓글 목록 조회 (삭제되지 않은 댓글만)
     */
    @Query("SELECT c FROM CommunityComment c WHERE c.community.id = :communityId AND c.isDeleted = false ORDER BY c.createdAt ASC")
    Page<CommunityComment> findByCommunityIdAndNotDeleted(@Param("communityId") Long communityId, Pageable pageable);
    
    /**
     * 특정 커뮤니티 게시글의 댓글 목록 조회 (삭제되지 않은 댓글만, 리스트)
     */
    @Query("SELECT c FROM CommunityComment c WHERE c.community.id = :communityId AND c.isDeleted = false ORDER BY c.createdAt ASC")
    List<CommunityComment> findByCommunityIdAndNotDeleted(@Param("communityId") Long communityId);
    
    /**
     * 특정 커뮤니티 게시글의 댓글 수 조회 (삭제되지 않은 댓글만)
     */
    @Query("SELECT COUNT(c) FROM CommunityComment c WHERE c.community.id = :communityId AND c.isDeleted = false")
    Long countByCommunityIdAndNotDeleted(@Param("communityId") Long communityId);
    
    /**
     * 사용자가 작성한 댓글 목록 조회 (삭제되지 않은 댓글만)
     */
    @Query("SELECT c FROM CommunityComment c WHERE c.userId = :userId AND c.isDeleted = false ORDER BY c.createdAt DESC")
    Page<CommunityComment> findByUserIdAndNotDeleted(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 사용자가 댓글을 단 커뮤니티 게시글들 조회 (중복 제거, 최신순)
     */
    @Query(value = "SELECT DISTINCT c.* FROM communities c WHERE c.id IN (SELECT DISTINCT cc.community_id FROM community_comments cc WHERE cc.user_id = ?1 AND cc.is_deleted = false) ORDER BY c.created_at DESC", 
           countQuery = "SELECT COUNT(DISTINCT c.id) FROM communities c WHERE c.id IN (SELECT DISTINCT cc.community_id FROM community_comments cc WHERE cc.user_id = ?1 AND cc.is_deleted = false)",
           nativeQuery = true)
    Page<Community> findCommunitiesByUserComments(Long userId, Pageable pageable);
    
    /**
     * 커뮤니티 게시글 삭제 시 관련 댓글들 논리적 삭제
     */
    @Query("UPDATE CommunityComment c SET c.isDeleted = true WHERE c.community.id = :communityId")
    void deleteAllByCommunityId(@Param("communityId") Long communityId);
}