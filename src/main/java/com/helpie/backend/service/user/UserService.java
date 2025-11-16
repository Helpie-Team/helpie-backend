package com.helpie.backend.service.user;

import com.helpie.backend.domain.email.AuthType;
import com.helpie.backend.domain.user.User;
import com.helpie.backend.domain.user.UserRole;
import com.helpie.backend.domain.user.UserVo;
import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.ErrorCode;
import com.helpie.backend.repository.auth.EmailAuthRepository;
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
    private final EmailAuthRepository emailAuthRepository;

    public UserVo findUserVo(Long memberId) {

        final var member = this.userCommonService.findById(memberId);

        return new UserVo(member.getId(), member.getUsername(), List.of(UserRole.USER));
    }

    @Transactional
    public Long createUser(
            String username,
            String email
    ) {

        if (this.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.ALREADY_EXIST_MEMBER, "이미 존재하는 이메일 입니다.") {
            };
        }

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

        if (this.existsByEmail(email)) {
            // 이메일 인증이 완료되지 않은 계정인지 확인
            if (!this.isEmailVerified(email)) {
                // 미인증 계정 삭제
                this.deleteUnverifiedUser(email);
            } else {
                // 인증 완료된 계정이면 중복 오류
                throw new BusinessException(ErrorCode.ALREADY_EXIST_MEMBER, "이미 존재하는 이메일 입니다.") {
                };
            }
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
    public void updatePassword(String email, String password, AuthType authType) {
        final var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND) {
                });
        user.updatePassword(encoder.encode(password));

        final var emailAuthByPW = emailAuthRepository.findByEmailAndAuthType(email, authType);
        emailAuthRepository.delete(emailAuthByPW);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return this.userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public Boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    /**
     * 이메일 인증이 완료되었는지 확인
     */
    @Transactional(readOnly = true)
    public boolean isEmailVerified(String email) {
        // EMAIL_AUTH 타입으로 인증이 완료된(expired=true) 기록이 있는지 확인
        return emailAuthRepository.existsByEmailAndAuthTypeAndExpired(
                email, 
                com.helpie.backend.domain.email.AuthType.EMAIL_AUTH, 
                true
        );
    }

    /**
     * 미인증 계정 삭제
     */
    @Transactional
    public void deleteUnverifiedUser(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            // 관련된 EmailAuth 기록도 삭제
            emailAuthRepository.deleteByEmailAndAuthType(
                    email, 
                    com.helpie.backend.domain.email.AuthType.EMAIL_AUTH
            );
            // 사용자 삭제
            userRepository.delete(user);
        });
    }
}


