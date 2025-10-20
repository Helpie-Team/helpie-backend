package com.helpie.backend.service.user;

import com.helpie.backend.domain.user.User;
import com.helpie.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserCommonService {
    private final UserRepository userRepository;

    public User findById(Long memberId) {
        return this.userRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
    }
}
