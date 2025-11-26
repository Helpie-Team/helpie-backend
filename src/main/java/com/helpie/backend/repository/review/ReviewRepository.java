package com.helpie.backend.repository.review;

import com.helpie.backend.domain.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByGroup_IdAndUser_Id(Long groupId, Long userId);
    
    /**
     * 사용자별 리뷰 조회 (페이징)
     */
    Page<Review> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 사용자가 작성한 총 리뷰 수
     */
    Integer countByUser_Id(Long userId);
    
    /**
     * 사용자가 작성한 리뷰의 평균 평점
     */
    @Query("SELECT AVG(r.rate) FROM Review r WHERE r.user.id = :userId")
    Double findAverageRatingByUserId(@Param("userId") Long userId);
    
    /**
     * 사용자가 작성한 특정 평점의 리뷰 수
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId AND r.rate = :rating")
    Integer countByUser_IdAndRate(@Param("userId") Long userId, @Param("rating") Integer rating);
}
