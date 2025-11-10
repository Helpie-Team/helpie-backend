package com.helpie.backend.repository.review;

import com.helpie.backend.dto.review.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewCustomRepository {
    Page<ReviewResponse> findAllReviews(Pageable pageable);
}
