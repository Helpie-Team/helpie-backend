package com.helpie.backend.service.sociallogin;

import com.helpie.backend.domain.sociallogin.SocialLogin;
import com.helpie.backend.domain.sociallogin.SocialType;
import com.helpie.backend.domain.user.UserVo;
import com.helpie.backend.dto.sociallogin.OAuthProfile;
import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.ErrorCode;
import com.helpie.backend.repository.sociallogin.SocialLoginRepository;
import com.helpie.backend.service.sociallogin.impl.GoogleOAuth2Provider;
import com.helpie.backend.service.sociallogin.impl.KakaoOAuth2Provider;
import com.helpie.backend.service.user.UserCommonService;
import com.helpie.backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialLoginService {
    private final SocialLoginRepository socialLoginRepository;
    private final KakaoOAuth2Provider kakaoOAuth2Provider;
    private final GoogleOAuth2Provider googleOAuth2Provider;
    private final UserService userService;
    private final UserCommonService userCommonService;

    @Transactional
    public UserVo findUserVoByCode(
            SocialType socialType,
            String code,
            String redirectUri
    ) {
        final String socialAccessToken;

        switch (socialType) {
            case GOOGLE:
                socialAccessToken = this.googleOAuth2Provider.getAccessToken(code, redirectUri);
                break;
            case KAKAO:
                socialAccessToken = this.kakaoOAuth2Provider.getAccessToken(code, redirectUri);
                break;
            default:
                throw new IllegalArgumentException("Unsupported social type: " + socialType);
        }

        final var profile = this.getProfile(
                socialAccessToken,
                socialType
        );

        //                        )
        final var socialLogin = this.socialLoginRepository.findByCodeAndSocialType(
                profile.getCode(),
                profile.getSocialType()
        ).orElseThrow(() ->
                new BusinessException(
                        ErrorCode.UNAUTHORIZED,
                        Map.of(
                                "socialType", socialType.name(),
                                "socialAccessToken", socialAccessToken,
                                "profile", profile
                        )
                ) {
                });
        return this.userService.findUserVo(socialLogin.getUser().getId());
    }

    @Transactional
    public Long create(
            Long memberId,
            SocialType socialType,
            String socialAccessToken
    ) {
        final var profile = this.getProfile(
                socialAccessToken,
                socialType
        );

        if (this.socialLoginRepository.existsByCodeAndSocialType(
                profile.getCode(),
                profile.getSocialType()
        )) {
            throw new RuntimeException("이미 존재하는 소셜 로그인 정보입니다.");
        }

        final var socialLogin = new SocialLogin(
                profile.getCode(),
                socialType,
                profile.getRawData(),
                this.userCommonService.findById(
                        memberId
                )
        );

        this.socialLoginRepository.save(socialLogin);

        return socialLogin.getId();
    }

    /**
     * TODO: 추후 더 객체지향 적으로 수정
     */
    private OAuthProfile getProfile(String socialAccessToken, SocialType socialType) {
        final OAuthProfile profile;

        switch (socialType) {
            case GOOGLE:
                profile = this.googleOAuth2Provider.getProfile(socialAccessToken);
                break;
            case KAKAO:
                profile = this.kakaoOAuth2Provider.getProfile(socialAccessToken);
                break;
            default:
                throw new IllegalArgumentException("Unsupported social type: " + socialType);
        }

        return profile;
    }
}