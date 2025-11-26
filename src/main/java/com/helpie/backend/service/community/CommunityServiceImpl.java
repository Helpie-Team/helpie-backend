package com.helpie.backend.service.community;

import com.helpie.backend.domain.community.Community;
import com.helpie.backend.domain.community.CommunityCategory;
import com.helpie.backend.domain.community.CommunityImage;
import com.helpie.backend.domain.community.CommunityComment;
import com.helpie.backend.domain.community.CommunityLike;
import com.helpie.backend.dto.community.CommunityCreateRequest;
import com.helpie.backend.dto.community.CommunityResponse;
import com.helpie.backend.dto.community.CommunityUpdateRequest;
import com.helpie.backend.dto.community.CommunityCommentRequest;
import com.helpie.backend.dto.community.CommunityCommentResponse;
import com.helpie.backend.repository.community.CommunityRepository;
import com.helpie.backend.repository.community.CommunityImageRepository;
import com.helpie.backend.repository.community.CommunityCommentRepository;
import com.helpie.backend.repository.community.CommunityLikeRepository;
import com.helpie.backend.service.file.FileService;
import com.helpie.backend.service.user.UserImageService;
import com.helpie.backend.service.notification.NotificationSettingService;
import com.helpie.backend.service.notification.NotificationService;
import com.helpie.backend.domain.user.UserImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 커뮤니티 서비스 구현체
 * 
 * @author 전우선
 * @since 2025-11-21(금)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityServiceImpl implements CommunityService {
    
    private final CommunityRepository communityRepository;
    private final CommunityImageRepository communityImageRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityLikeRepository communityLikeRepository;
    private final FileService fileService;
    private final UserImageService userImageService;
    private final NotificationSettingService notificationSettingService;
    private final NotificationService notificationService;
    
    @Override
    @Transactional
    public CommunityResponse createCommunity(Long userId, String username, CommunityCreateRequest request, List<MultipartFile> images) {
        // ALL 카테고리로는 게시글 작성 불가
        if (request.getCategory() == CommunityCategory.ALL) {
            throw new IllegalArgumentException("전체(ALL) 카테고리로는 게시글을 작성할 수 없습니다. INFO_SHARE 또는 FREE_BOARD를 선택해주세요.");
        }
        
        // 커뮤니티 게시글 생성
        Community community = Community.builder()
            .userId(userId)
            .username(username)
            .category(request.getCategory())
            .title(request.getTitle())
            .content(request.getContent())
            .build();
        
        Community savedCommunity = communityRepository.save(community);
        
        // 이미지 업로드 및 저장
        if (images != null && !images.isEmpty()) {
            uploadAndSaveImages(savedCommunity, images);
        }
        
        // 작성자 프로필 이미지 조회
        String userProfileImage = userImageService.getUserImage(userId)
            .map(UserImage::getImageUrl)
            .orElse(null);

        log.info("커뮤니티 게시글 작성 완료 - ID: {}, 작성자: {}", savedCommunity.getId(), username);
        return CommunityResponse.from(savedCommunity, userProfileImage);
    }
    
    @Override
    @Transactional
    public CommunityResponse getCommunityDetail(Long communityId) {
        Community community = communityRepository.findById(communityId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다: " + communityId));
        
        // 조회수 증가
        communityRepository.incrementViewCount(communityId);
        community.incrementViewCount();
        
        // 작성자 프로필 이미지 조회
        String userProfileImage = userImageService.getUserImage(community.getUserId())
            .map(UserImage::getImageUrl)
            .orElse(null);
        
        return CommunityResponse.from(community, userProfileImage);
    }
    
    @Override
    public Page<CommunityResponse> getCommunities(Pageable pageable) {
        return communityRepository.findAllByOrderByCreatedAtDesc(pageable)
            .map(community -> {
                String userProfileImage = userImageService.getUserImage(community.getUserId())
                    .map(UserImage::getImageUrl)
                    .orElse(null);
                return CommunityResponse.fromSummary(community, userProfileImage);
            });
    }
    
    @Override
    public Page<CommunityResponse> getCommunitiesByCategory(CommunityCategory category, Pageable pageable) {
        // ALL 카테고리인 경우 전체 조회로 리다이렉트
        if (category == CommunityCategory.ALL) {
            return getCommunities(pageable);
        }
        
        return communityRepository.findByCategoryOrderByCreatedAtDesc(category, pageable)
            .map(community -> {
                String userProfileImage = userImageService.getUserImage(community.getUserId())
                    .map(UserImage::getImageUrl)
                    .orElse(null);
                return CommunityResponse.fromSummary(community, userProfileImage);
            });
    }
    
    @Override
    public Page<CommunityResponse> getMyCommunities(Long userId, Pageable pageable) {
        return communityRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
            .map(community -> {
                String userProfileImage = userImageService.getUserImage(community.getUserId())
                    .map(UserImage::getImageUrl)
                    .orElse(null);
                return CommunityResponse.fromSummary(community, userProfileImage);
            });
    }
    
    @Override
    public Page<CommunityResponse> searchCommunities(String keyword, Pageable pageable) {
        return communityRepository.findByTitleOrContentContaining(keyword, pageable)
            .map(community -> {
                String userProfileImage = userImageService.getUserImage(community.getUserId())
                    .map(UserImage::getImageUrl)
                    .orElse(null);
                return CommunityResponse.fromSummary(community, userProfileImage);
            });
    }
    
    @Override
    public Page<CommunityResponse> searchCommunitiesByCategory(CommunityCategory category, String keyword, Pageable pageable) {
        // ALL 카테고리인 경우 전체 검색으로 리다이렉트
        if (category == CommunityCategory.ALL) {
            return searchCommunities(keyword, pageable);
        }
        
        return communityRepository.findByCategoryAndTitleOrContentContaining(category, keyword, pageable)
            .map(community -> {
                String userProfileImage = userImageService.getUserImage(community.getUserId())
                    .map(UserImage::getImageUrl)
                    .orElse(null);
                return CommunityResponse.fromSummary(community, userProfileImage);
            });
    }
    
    @Override
    @Transactional
    public CommunityResponse updateCommunity(Long communityId, Long userId, CommunityUpdateRequest request, List<MultipartFile> images) {
        Community community = communityRepository.findById(communityId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다: " + communityId));
        
        // 작성자 확인
        if (!community.getUserId().equals(userId)) {
            throw new IllegalArgumentException("게시글을 수정할 권한이 없습니다");
        }
        
        // ALL 카테고리로는 게시글 수정 불가
        if (request.getCategory() == CommunityCategory.ALL) {
            throw new IllegalArgumentException("전체(ALL) 카테고리로는 게시글을 수정할 수 없습니다. INFO_SHARE 또는 FREE_BOARD를 선택해주세요.");
        }
        
        // 게시글 수정
        community.updatePost(request.getTitle(), request.getContent(), request.getCategory());
        
        // 기존 이미지 삭제 후 새 이미지 업로드
        if (images != null && !images.isEmpty()) {
            communityImageRepository.deleteByCommunityId(communityId);
            uploadAndSaveImages(community, images);
        }
        
        // 작성자 프로필 이미지 조회
        String userProfileImage = userImageService.getUserImage(userId)
            .map(UserImage::getImageUrl)
            .orElse(null);

        log.info("커뮤니티 게시글 수정 완료 - ID: {}, 작성자: {}", communityId, userId);
        return CommunityResponse.from(community, userProfileImage);
    }
    
    @Override
    @Transactional
    public void deleteCommunity(Long communityId, Long userId) {
        Community community = communityRepository.findById(communityId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다: " + communityId));
        
        // 작성자 확인
        if (!community.getUserId().equals(userId)) {
            throw new IllegalArgumentException("게시글을 삭제할 권한이 없습니다");
        }
        
        // 이미지 먼저 삭제
        communityImageRepository.deleteByCommunityId(communityId);
        
        // 게시글 삭제
        communityRepository.delete(community);
        
        log.info("커뮤니티 게시글 삭제 완료 - ID: {}, 작성자: {}", communityId, userId);
    }
    
    @Override
    public List<CommunityResponse> getPopularCommunities() {
        return communityRepository.findTop5ByOrderByViewCountDescCreatedAtDesc()
            .stream()
            .map(community -> {
                String userProfileImage = userImageService.getUserImage(community.getUserId())
                    .map(UserImage::getImageUrl)
                    .orElse(null);
                return CommunityResponse.fromSummary(community, userProfileImage);
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public List<CommunityResponse> getRecommendedCommunities() {
        Pageable topFive = Pageable.ofSize(5);
        return communityRepository.findTop5ByLikesCount(topFive)
            .stream()
            .map(community -> {
                String userProfileImage = userImageService.getUserImage(community.getUserId())
                    .map(UserImage::getImageUrl)
                    .orElse(null);
                return CommunityResponse.fromSummary(community, userProfileImage);
            })
            .collect(Collectors.toList());
    }
    
    
    /**
     * 이미지 업로드 및 저장
     */
    private void uploadAndSaveImages(Community community, List<MultipartFile> images) {
        if (images.size() > 4) {
            throw new IllegalArgumentException("이미지는 최대 4개까지 첨부 가능합니다");
        }
        
        for (int i = 0; i < images.size(); i++) {
            MultipartFile image = images.get(i);
            if (!image.isEmpty()) {
                String imageUrl = fileService.uploadFile(image);
                
                CommunityImage communityImage = CommunityImage.builder()
                    .community(community)
                    .imageUrl(imageUrl)
                    .originalFilename(image.getOriginalFilename())
                    .fileSize(image.getSize())
                    .displayOrder(i + 1)
                    .build();
                
                community.addImage(communityImage);
                communityImageRepository.save(communityImage);
            }
        }
    }
    
    @Override
    @Transactional
    public CommunityCommentResponse createComment(Long communityId, Long userId, String username, CommunityCommentRequest request) {
        log.debug("댓글 작성 시작 - communityId: {}, userId: {}", communityId, userId);
        
        // 게시글 존재 확인
        Community community = communityRepository.findById(communityId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다: " + communityId));
        
        // 댓글 생성
        CommunityComment comment = new CommunityComment(community, userId, username, request.content());
        CommunityComment savedComment = communityCommentRepository.save(comment);
        
        // 게시글 작성자와 댓글 작성자가 다르고, 댓글 알림이 활성화된 경우 알림 발송
        if (!community.getUserId().equals(userId) && 
            notificationSettingService.canReceiveCommentNotification(community.getUserId())) {
            notificationService.sendCommentNotification(
                community.getUserId(), 
                communityId, 
                community.getTitle(), 
                userId, 
                username
            );
        }
        
        log.info("댓글 작성 완료 - commentId: {}", savedComment.getId());
        
        // 프로필 이미지 조회
        String profileImage = userImageService.getUserImageUrl(userId);
        
        return CommunityCommentResponse.from(savedComment, profileImage);
    }
    
    @Override
    public Page<CommunityCommentResponse> getComments(Long communityId, Pageable pageable) {
        log.debug("댓글 목록 조회 - communityId: {}", communityId);
        
        Page<CommunityComment> comments = communityCommentRepository.findByCommunityIdAndNotDeleted(communityId, pageable);
        
        return comments.map(comment -> {
            String profileImage = userImageService.getUserImageUrl(comment.getUserId());
            return CommunityCommentResponse.from(comment, profileImage);
        });
    }
    
    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        log.debug("댓글 삭제 시작 - commentId: {}, userId: {}", commentId, userId);
        
        CommunityComment comment = communityCommentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다: " + commentId));
        
        // 작성자 확인
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다");
        }
        
        // 논리적 삭제
        comment.delete();
        communityCommentRepository.save(comment);
        
        log.info("댓글 삭제 완료 - commentId: {}", commentId);
    }
    
    @Override
    @Transactional
    public boolean toggleLike(Long communityId, Long userId, String username) {
        log.debug("좋아요 토글 시작 - communityId: {}, userId: {}", communityId, userId);
        
        // 게시글 존재 확인
        Community community = communityRepository.findById(communityId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다: " + communityId));
        
        // 기존 좋아요 확인
        Optional<CommunityLike> existingLike = communityLikeRepository.findByCommunityIdAndUserId(communityId, userId);
        
        if (existingLike.isPresent()) {
            // 좋아요 취소
            communityLikeRepository.delete(existingLike.get());
            log.info("좋아요 취소 완료 - communityId: {}, userId: {}", communityId, userId);
            return false;
        } else {
            // 좋아요 추가
            CommunityLike like = new CommunityLike(community, userId, username);
            communityLikeRepository.save(like);
            
            // 게시글 작성자와 좋아요 작성자가 다르고, 좋아요 알림이 활성화된 경우 알림 발송
            if (!community.getUserId().equals(userId) && 
                notificationSettingService.canReceiveLikeNotification(community.getUserId())) {
                notificationService.sendLikeNotification(
                    community.getUserId(), 
                    communityId, 
                    community.getTitle(), 
                    userId, 
                    username
                );
            }
            
            log.info("좋아요 추가 완료 - communityId: {}, userId: {}", communityId, userId);
            return true;
        }
    }
    
    @Override
    public boolean isLikedByUser(Long communityId, Long userId) {
        return communityLikeRepository.existsByCommunityIdAndUserId(communityId, userId);
    }
}