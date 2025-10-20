package com.helpie.backend.service.sociallogin;


import com.helpie.backend.dto.sociallogin.OAuthProfile;

public interface OAuth2Provider {
    String getAccessToken(String code, String redirectUri);
    OAuthProfile getProfile(String accessToken);
}
