package com.helpie.backend.dto.sociallogin;

import com.helpie.backend.domain.sociallogin.SocialType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class OAuthProfile {
    private final String code;
    private final Map<String, Object> rawData;
    private final SocialType socialType;
}
