package com.helpie.backend.repository.community;

import com.helpie.backend.domain.community.CommunityImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 커뮤니티 이미지 Repository
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Repository
public interface CommunityImageRepository extends JpaRepository<CommunityImage, Long> {
    
    /**
     * 커뮤니티 게시글별 이미지 조회 (표시 순서대로)
     */
    List<CommunityImage> findByCommunityIdOrderByDisplayOrderAsc(Long communityId);
    
    /**
     * 커뮤니티 게시글별 이미지 삭제
     */
    void deleteByCommunityId(Long communityId);
}