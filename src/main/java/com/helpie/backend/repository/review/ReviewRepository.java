package com.helpie.backend.repository.review;

import com.helpie.backend.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByGroup_IdAndUser_Id(Long groupId, Long userId);
}
