package com.helpie.backend.service.auth;

import com.helpie.backend.domain.sociallogin.SocialType;
import com.helpie.backend.domain.user.RefreshToken;
import com.helpie.backend.domain.user.User;
import com.helpie.backend.domain.user.UserJwtClaim;
import com.helpie.backend.dto.auth.HttpSigninInResponse;
import com.helpie.backend.dto.auth.SignInRequest;
import com.helpie.backend.dto.auth.SignUpRequest;
import com.helpie.backend.dto.sociallogin.SigninResponse;
import com.helpie.backend.dto.sociallogin.SignupByCodeRequest;
import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.ErrorCode;
import com.helpie.backend.repository.user.RefreshTokenRepository;
import com.helpie.backend.repository.user.UserRepository;
import com.helpie.backend.service.sociallogin.SocialLoginService;
import com.helpie.backend.service.user.UserCommonService;
import com.helpie.backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserCommonService userCommonService;
    private final SocialLoginService socialLoginService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public SigninResponse signin(Long userId) {
        return new SigninResponse(
                this.generateAccessToken(userId),
                this.generateRefreshToken(userId)
        );
    }
    @Transactional
    public SigninResponse signup(
            SocialType socialType,
            SignupByCodeRequest signupByCodeRequest
    ) {
        final Long memberId = this.userService.createUser(
                signupByCodeRequest.username(),
                signupByCodeRequest.email()
        );
        this.socialLoginService.create(
                memberId,
                socialType,
                signupByCodeRequest.socialAccessToken()
        );

        return this.signin(memberId);
    }

    public SigninResponse signin(SignInRequest signinRequest) {
        final var user = userRepository.findByEmail(signinRequest.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND) {
                });
        validatePassword(signinRequest.password(), user.getPassword());
        final var userId = this.userCommonService.findById(user.getId()).getId();

        return new SigninResponse(
                this.generateAccessToken(userId),
                this.generateRefreshToken(userId)
        );
    }

    public SigninResponse signup(SignUpRequest signUpRequest) {
        final Long memberId = this.userService.createUser(
                signUpRequest.username(),
                signUpRequest.email(),
                encoder.encode(signUpRequest.password())
        );

        return this.signin(memberId);
    }

    public String generateAccessToken(Long memberId) {
        final var userVo = this.userService.findUserVo(memberId);

        final var userClaim = new UserJwtClaim(
                userVo.getId(),
                userVo.getUsername(),
                userVo.getUserRoles()
        );

        return this.jwtTokenProvider.createToken(userClaim);
    }

    public String generateAccessToken(String refreshToken) {
        this.verifyRefreshToken(refreshToken);

        final var rt = this.refreshTokenRepository.findByToken(refreshToken).get();

        return this.generateAccessToken(rt.getUser().getId());
    }

    @Transactional
    public Optional<String> generateRefreshTokenOrEmpty(String refreshToken) {
        this.verifyRefreshToken(refreshToken);

        final var rt = this.refreshTokenRepository.findByToken(refreshToken).get();

        if (!LocalDateTime.now().minus(Duration.ofDays(3)).isAfter(rt.getExpiredAt())) {
            return Optional.empty();
        }

        this.removeRefreshToken(rt.getToken());

        return Optional.of(
                this.generateRefreshToken(rt.getUser().getId())
        );
    }

    @Transactional
    public String generateRefreshToken(Long memberId) {
        final var user = this.userCommonService.findById(memberId);

        final var refreshToken = DigestUtils
                .md5DigestAsHex(UUID.randomUUID().toString().getBytes());

        final var refreshTokenEntity = new RefreshToken(
                refreshToken,
                user,
                Duration.ofDays(14)
        );

        this.refreshTokenRepository.save(refreshTokenEntity);

        return refreshToken;
    }

    private void verifyRefreshToken(String refreshToken) {
        final var rt = this.refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("리프레쉬 토큰을 찾을 없습니다."));

        if (LocalDateTime.now().isAfter(rt.getExpiredAt())) {
            throw new RuntimeException("리프레쉬 토큰이 만료되었습니다.");
        }
    }

    private void validatePassword(String requestPassword, String encodedPassword) {
        if (!encoder.matches(requestPassword, encodedPassword)) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD) {
            };
        }
    }


    @Transactional
    public void removeRefreshToken(String refreshToken) {
        this.refreshTokenRepository.deleteByToken(refreshToken);
    }
}
