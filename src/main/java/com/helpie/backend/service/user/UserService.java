package com.helpie.backend.service.user;

import com.helpie.backend.domain.user.User;
import com.helpie.backend.domain.user.UserRole;
import com.helpie.backend.domain.user.UserVo;
import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.ErrorCode;
import com.helpie.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserCommonService userCommonService;
    private final BCryptPasswordEncoder encoder;

    public UserVo findUserVo(Long memberId) {

        final var member = this.userCommonService.findById(memberId);

        return new UserVo(member.getId(), member.getUsername(), List.of(UserRole.USER));
    }

    @Transactional
    public Long createUser(
            String username,
            String email
    ) {
        if (this.existsByUsername(username)) {
            throw new BusinessException(ErrorCode.ALREADY_EXIST_MEMBER, "이미 존재하는 유저입니다.") {
            };

        }

        final var user = new User(username, email);

        this.userRepository.save(user);

        return user.getId();
    }

    @Transactional
    public Long createUser(
            String username,
            String email,
            String password
    ) {
        if (this.existsByUsername(username)) {
            throw new BusinessException(ErrorCode.ALREADY_EXIST_MEMBER, "이미 존재하는 유저입니다.") {
            };

        }

        final var user = new User(username, email, password);

        this.userRepository.save(user);

        return user.getId();
    }

    @Transactional
    public void updateUsername(Long userId, String username) {
        final var user = this.userCommonService.findById(userId);
        user.updateUsername(username);
    }

    @Transactional
    public void updatePassword(String email, String password) {
        final var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND) {
                });
        user.updatePassword(encoder.encode(password));
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return this.userRepository.existsByUsername(username);
    }
}


