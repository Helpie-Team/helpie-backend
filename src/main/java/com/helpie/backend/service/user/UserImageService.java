package com.helpie.backend.service.user;

import com.helpie.backend.domain.user.UserImage;
import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.ErrorCode;
import com.helpie.backend.repository.user.UserImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserImageService {
    private final UserImageRepository userImageRepository;

    @Transactional
    public void saveUserImage(Long userId, String imageUrl) {
        Optional<UserImage> userImageData = userImageRepository.findByUserId(userId);
        if (userImageData.isEmpty()) {
            final var userImage = UserImage.builder()
                    .userId(userId)
                    .imageUrl(imageUrl).build();
            userImageRepository.save(userImage);
        } else {
            final var userImage = userImageData.get();
            userImage.updateImageUrl(imageUrl);
        }
    }

    @Transactional
    public void resetUserImage(Long userId) {
        final var userImageData = userImageRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR) {
                });
        userImageData.resetImageUrl();
    }

    public Optional<UserImage> getUserImage(Long userId) {
        return userImageRepository.findByUserId(userId);
    }
    
    /**
     * 사용자의 프로필 이미지 URL 조회
     * 이미지가 없는 경우 null 반환
     */
    public String getUserImageUrl(Long userId) {
        return userImageRepository.findByUserId(userId)
            .map(UserImage::getImageUrl)
            .orElse(null);
    }
}
