package com.helpie.backend.service.sociallogin.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helpie.backend.domain.sociallogin.SocialType;
import com.helpie.backend.dto.sociallogin.OAuthProfile;
import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.ErrorCode;
import com.helpie.backend.service.sociallogin.OAuth2Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuth2Provider implements OAuth2Provider {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${google.oauth.client-id}")
    private String clientId;

    @Value("${google.oauth.client-secret}")
    private String clientSecret;

    @Value("${google.oauth.token-url:https://oauth2.googleapis.com/token}")
    private String tokenUrl;

    @Value("${google.oauth.userinfo-url:https://openidconnect.googleapis.com/v1/userinfo}")
    private String userInfoUrl;

    /**
     * Authorization Code -> access_token 교환
     */
    @Override
    public String getAccessToken(String code, String redirectUri) {
        var form = new LinkedMultiValueMap<String, Object>();
        form.set("grant_type", "authorization_code");
        form.set("client_id", this.clientId);
        form.set("client_secret", this.clientSecret); // public client(PKCE 전용)라면 제외
        form.set("code", code);
        form.set("redirect_uri", redirectUri);
        // PKCE 사용 시: form.set("code_verifier", codeVerifier);

        var headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        try {
            var res = this.restTemplate.exchange(
                    this.tokenUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(form, headers),
                    Map.class
            );
            var body = Objects.requireNonNull(res.getBody());
            return Objects.requireNonNull((String) body.get("access_token"));
        } catch (HttpClientErrorException ex) {
            throw googleExceptionConverter(ex);
        }
    }

    /**
     * access_token으로 UserInfo 조회 (sub/email/name/picture 등)
     */
    @Override
    public OAuthProfile getProfile(String accessToken) {
        var headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        try {
            var res = this.restTemplate.exchange(
                    this.userInfoUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class
            );
            var body = Objects.requireNonNull(res.getBody());

            String id = String.valueOf(body.get("sub"));

            return new OAuthProfile(
                    id,
                    body,
                    SocialType.GOOGLE
            );
        } catch (HttpClientErrorException ex) {
            throw googleExceptionConverter(ex);
        }
    }

    /**
     * Google OAuth 오류를 도메인 예외로 변환
     * error 예시: invalid_grant, redirect_uri_mismatch, unauthorized_client, invalid_client ...
     */
    private RuntimeException googleExceptionConverter(HttpClientErrorException ex) {
        try {
            var raw = ex.getResponseBodyAsString();
            var err = this.objectMapper.readValue(raw, Map.class);
            var error = (String) err.get("error");                 // e.g. "invalid_grant"
            var errorDesc = (String) err.get("error_description"); // 상세 메시지

            if (error != null) {
                // 프로젝트의 ErrorCode에 맞게 매핑
                switch (error) {
                    case "invalid_grant": // code가 만료/부정확
                        return new BusinessException(ErrorCode.NOT_MATCH_OAUTH_CODE) {
                        };
                    case "redirect_uri_mismatch":
                        return new BusinessException(ErrorCode.NOT_ALLOW_OAUTH_REDIRECT_URI) {
                        };
                    case "invalid_client":
                    case "unauthorized_client":
                        return new BusinessException(ErrorCode.NOT_MATCH_SOCIAL_MEMBER) {
                        };
                    default:
                        log.warn("Google OAuth error: {} - {}", error, errorDesc);
                }
            }
            log.error(ex.getMessage(), ex);
            return new RuntimeException("서버 에러가 발생했습니다.");
        } catch (JsonProcessingException e) {
            log.error(ex.getMessage(), ex);
            return new RuntimeException(e);
        }
    }
}
