package com.helpie.backend.service.auth;

import com.helpie.backend.domain.sociallogin.SocialType;
import com.helpie.backend.domain.user.RefreshToken;
import com.helpie.backend.domain.user.UserJwtClaim;
import com.helpie.backend.dto.sociallogin.SigninResponse;
import com.helpie.backend.dto.sociallogin.SignupByCodeRequest;
import com.helpie.backend.repository.user.RefreshTokenRepository;
import com.helpie.backend.service.sociallogin.SocialLoginService;
import com.helpie.backend.service.user.UserCommonService;
import com.helpie.backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public SigninResponse signin(Long memberId) {
        return new SigninResponse(
                this.generateAccessToken(memberId),
                this.generateRefreshToken(memberId)
        );
    }
    @Transactional
    public SigninResponse signup(
            SocialType socialType,
            SignupByCodeRequest signupByCodeRequest
    ) {
        final Long memberId = this.userService.createUser(
                signupByCodeRequest.username()
        );
        this.socialLoginService.create(
                memberId,
                socialType,
                signupByCodeRequest.socialAccessToken()
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

    @Transactional
    public void removeRefreshToken(String refreshToken) {
        this.refreshTokenRepository.deleteByToken(refreshToken);
    }
}
