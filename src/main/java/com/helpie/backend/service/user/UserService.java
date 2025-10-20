package com.helpie.backend.service.user;

import com.helpie.backend.domain.user.User;
import com.helpie.backend.domain.user.UserRole;
import com.helpie.backend.domain.user.UserVo;
import com.helpie.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserCommonService userCommonService;

    public UserVo findUserVo(Long memberId) {

        final var member = this.userCommonService.findById(memberId);

        return new UserVo(member.getId(), member.getUsername(), List.of(UserRole.USER));
    }

    @Transactional
    public Long createUser(
            String username
    ) {
        if (this.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 유저입니다.");
        }

        final var user = new User(username);

        this.userRepository.save(user);

        return user.getId();
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return this.userRepository.existsByUsername(username);
    }
}


