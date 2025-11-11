package com.helpie.backend.service.review;

import com.helpie.backend.domain.group.Group;
import com.helpie.backend.domain.review.Review;
import com.helpie.backend.domain.review.ReviewImage;
import com.helpie.backend.domain.user.User;
import com.helpie.backend.dto.review.ReviewCreateRequest;
import com.helpie.backend.dto.review.ReviewCreateResponse;
import com.helpie.backend.dto.review.ReviewResponse;
import com.helpie.backend.repository.group.GroupRepository;
import com.helpie.backend.repository.review.ReviewCustomRepository;
import com.helpie.backend.repository.review.ReviewRepository;
import com.helpie.backend.repository.user.UserRepository;
import com.helpie.backend.service.file.FileService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ReviewCustomRepository reviewCustomRepository;
    private final FileService fileService;

    @Transactional
    public ReviewCreateResponse createReview(Long userId,Long groupId, ReviewCreateRequest request, List<MultipartFile> images) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. userId=" + userId));

        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다. groupId=" + groupId));

       Review review=Review.builder()
           .rate(request.rate())
           .description(request.description())
           .user(user)
           .group(group)
           .anonymityYn(request.anonymityYn())
           .build();

        Review saved=reviewRepository.save(review);
       //이미지 저장
        List<String> urls;
        try {
            urls = fileService.uploadFiles(images);
            for (String url : urls) {
                ReviewImage image = new ReviewImage(review, url);
                review.addImage(image);
            }

        } catch (RuntimeException e) {
            log.warn("이미지를 업로드하지 않았습니다");
            urls = new ArrayList<>();
        }

        return new ReviewCreateResponse(
            saved.getId(),
            saved.getRate(),
            saved.getDescription(),
            urls
        );

    }

    public Page<ReviewResponse> getReview(Pageable pageable) {
        return reviewCustomRepository.findAllReviews(pageable);
    }

}
