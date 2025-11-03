package com.helpie.backend.filter;

import com.helpie.backend.domain.user.UserJwtClaim;
import com.helpie.backend.domain.user.UserVo;
import com.helpie.backend.service.auth.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Optional;

public class JwtFilter extends GenericFilterBean {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtTokenProvider jwtTokenProvider;

    public JwtFilter(
            JwtTokenProvider jwtTokenProvider
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        resolveToken(httpServletRequest)
                .filter(jwtTokenProvider::validateToken)
                .map((token) -> jwtTokenProvider.decodeJwt(token, UserJwtClaim.class))
                .ifPresent((userJwtClaim) -> {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            new UserVo(
                                    userJwtClaim.getId(),
                                    userJwtClaim.getUsername(),
                                    userJwtClaim.getUserRoles()
                            ),
                            null,
                            userJwtClaim.getUserRoles()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });

        chain.doFilter(request, response);
    }

    private Optional<String> resolveToken(HttpServletRequest request) {
        final String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        final String tokenType = "Bearer";

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(tokenType + " ")) {
            return Optional.of(bearerToken.substring(tokenType.length() + 1));
        }

        return Optional.empty();
    }

}