package com.helpie.backend.repository.community;

import com.helpie.backend.domain.community.CommunityLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 커뮤니티 좋아요 리포지토리
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Repository
public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {
    
    /**
     * 특정 사용자의 특정 게시글 좋아요 조회
     */
    @Query("SELECT l FROM CommunityLike l WHERE l.community.id = :communityId AND l.userId = :userId")
    Optional<CommunityLike> findByCommunityIdAndUserId(@Param("communityId") Long communityId, @Param("userId") Long userId);
    
    /**
     * 특정 게시글의 좋아요 수 조회
     */
    @Query("SELECT COUNT(l) FROM CommunityLike l WHERE l.community.id = :communityId")
    Long countByCommunityId(@Param("communityId") Long communityId);
    
    /**
     * 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
     */
    @Query("SELECT COUNT(l) > 0 FROM CommunityLike l WHERE l.community.id = :communityId AND l.userId = :userId")
    boolean existsByCommunityIdAndUserId(@Param("communityId") Long communityId, @Param("userId") Long userId);
    
    /**
     * 커뮤니티 게시글 삭제 시 관련 좋아요들 삭제
     */
    void deleteByCommunityId(Long communityId);
    
    /**
     * 특정 사용자의 특정 게시글 좋아요 삭제
     */
    void deleteByCommunityIdAndUserId(Long communityId, Long userId);
}