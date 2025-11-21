package com.helpie.backend.service.community;

import com.helpie.backend.domain.community.CommunityCategory;
import com.helpie.backend.dto.community.CommunityCreateRequest;
import com.helpie.backend.dto.community.CommunityResponse;
import com.helpie.backend.dto.community.CommunityUpdateRequest;
import com.helpie.backend.dto.community.CommunityCommentRequest;
import com.helpie.backend.dto.community.CommunityCommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 커뮤니티 서비스 인터페이스
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
public interface CommunityService {
    
    /**
     * 커뮤니티 게시글 작성
     */
    CommunityResponse createCommunity(Long userId, String username, CommunityCreateRequest request, List<MultipartFile> images);
    
    /**
     * 커뮤니티 게시글 상세 조회
     */
    CommunityResponse getCommunityDetail(Long communityId);
    
    /**
     * 커뮤니티 게시글 목록 조회 (전체)
     */
    Page<CommunityResponse> getCommunities(Pageable pageable);
    
    /**
     * 커뮤니티 게시글 목록 조회 (카테고리별)
     */
    Page<CommunityResponse> getCommunitiesByCategory(CommunityCategory category, Pageable pageable);
    
    /**
     * 내 게시글 조회
     */
    Page<CommunityResponse> getMyCommunities(Long userId, Pageable pageable);
    
    /**
     * 게시글 검색 (제목 + 내용)
     */
    Page<CommunityResponse> searchCommunities(String keyword, Pageable pageable);
    
    /**
     * 카테고리별 게시글 검색
     */
    Page<CommunityResponse> searchCommunitiesByCategory(CommunityCategory category, String keyword, Pageable pageable);
    
    /**
     * 게시글 수정
     */
    CommunityResponse updateCommunity(Long communityId, Long userId, CommunityUpdateRequest request, List<MultipartFile> images);
    
    /**
     * 게시글 삭제
     */
    void deleteCommunity(Long communityId, Long userId);
    
    /**
     * 실시간 인기글 조회 (조회수 기준)
     */
    List<CommunityResponse> getPopularCommunities();
    
    /**
     * 추천글 조회 (좋아요 수 기준)
     */
    List<CommunityResponse> getRecommendedCommunities();
    
    /**
     * 댓글 작성
     */
    CommunityCommentResponse createComment(Long communityId, Long userId, String username, CommunityCommentRequest request);
    
    /**
     * 댓글 목록 조회
     */
    Page<CommunityCommentResponse> getComments(Long communityId, Pageable pageable);
    
    /**
     * 댓글 삭제
     */
    void deleteComment(Long commentId, Long userId);
    
    /**
     * 좋아요 토글
     */
    boolean toggleLike(Long communityId, Long userId, String username);
    
    /**
     * 좋아요 상태 확인
     */
    boolean isLikedByUser(Long communityId, Long userId);
}