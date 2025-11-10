package com.helpie.backend.repository.review.impl;

import static com.helpie.backend.domain.group.QGroup.group;
import static com.helpie.backend.domain.review.QReview.review;
import static com.helpie.backend.domain.review.QReviewImage.reviewImage;
import static com.helpie.backend.domain.user.QUser.user;
import static com.helpie.backend.domain.user.QUserImage.userImage;

import com.helpie.backend.dto.review.ReviewResponse;
import com.helpie.backend.repository.review.ReviewCustomRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReviewResponse> findAllReviews(Pageable pageable) {

        Long total = Optional.ofNullable(
            queryFactory
                .select(review.count())
                .from(review)
                .fetchOne()
        ).orElse(0L);

        List<Tuple> tuples = queryFactory
            .select(
                review.id,
                user.username,
                group.title,
                review.rate,
                review.description,
                group.meetingDate,
                reviewImage.imageUrl,
                userImage.imageUrl
            )
            .from(review)
            .join(review.user, user)
            .join(review.group, group)
            .leftJoin(review.images, reviewImage)
            .leftJoin(userImage).on(userImage.userId.eq(user.id))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(review.createdAt.desc())
            .fetch();

        Map<Long, ReviewResponseBuilder> map = new LinkedHashMap<>();

        for (Tuple tuple : tuples) {
            Long reviewId = tuple.get(review.id);

            ReviewResponseBuilder builder = map.computeIfAbsent(reviewId, id -> {
                String reviewerName = tuple.get(user.username);
                String groupTitle = tuple.get(group.title);
                Integer rate = tuple.get(review.rate);
                String description = tuple.get(review.description);
                LocalDateTime meetingDate = tuple.get(group.meetingDate);
                String profileImageUrl = tuple.get(userImage.imageUrl);

                return new ReviewResponseBuilder(
                    id,
                    reviewerName,
                    groupTitle,
                    rate,
                    description,
                    meetingDate,
                    profileImageUrl
                );
            });

            String reviewImageUrl = tuple.get(reviewImage.imageUrl);
            if (reviewImageUrl != null) {
                builder.addReviewImage(reviewImageUrl);
            }
        }

        List<ReviewResponse> content = new ArrayList<>();
        for (ReviewResponseBuilder builder : map.values()) {
            content.add(builder.build());
        }

        return new PageImpl<>(content, pageable, total);
    }

    private static class ReviewResponseBuilder {
        private final Long reviewId;
        private final String reviewerName;
        private final String groupTitle;
        private final Integer rate;
        private final String description;
        private final LocalDateTime meetingDate;
        private final String profileImage;
        private final List<String> reviewImages = new ArrayList<>();

        private ReviewResponseBuilder(Long reviewId,
            String reviewerName,
            String groupTitle,
            Integer rate,
            String description,
            LocalDateTime meetingDate,
            String profileImage) {
            this.reviewId = reviewId;
            this.reviewerName = reviewerName;
            this.groupTitle = groupTitle;
            this.rate = rate;
            this.description = description;
            this.meetingDate = meetingDate;
            this.profileImage = profileImage;
        }

        public void addReviewImage(String imageUrl) {
            this.reviewImages.add(imageUrl);
        }

        public ReviewResponse build() {
            return new ReviewResponse(
                reviewId,
                reviewerName,
                groupTitle,
                rate,
                description,
                meetingDate,
                reviewImages,
                profileImage
            );
        }
    }

}
