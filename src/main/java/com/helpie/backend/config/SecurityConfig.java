package com.helpie.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helpie.backend.dto.global.Response;
import com.helpie.backend.exception.BusinessException;
import com.helpie.backend.exception.ErrorCode;
import com.helpie.backend.filter.JwtFilter;
import com.helpie.backend.service.auth.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Spring Security 설정 클래스
 * 
 * 개발 환경에서 빠른 개발을 위해 모든 보안 기능을 비활성화한 상태입니다.
 * 추후 소셜 로그인 구현 시 CSRF 보호 및 인증/인가 설정을 추가해야 합니다.
 * 
 * TODO: 프로덕션 배포 전 다음 설정들을 적용해야 합니다:
 * - CSRF 보호 활성화 (소셜 로그인 시 필수)
 * - OAuth2 소셜 로그인 설정
 * - JWT 토큰 기반 인증 필터 추가
 * - 역할 기반 접근 제어 (RBAC) 설정
 * - 보안 헤더 설정 (CORS, XSS Protection 등)
 * 
 * @author 전우선
 * @since 2025-10-14(화)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final List<String> allowOriginHosts;

    private final AuthenticationEntryPoint unauthorizedEntryPoint =
            (request, response, authException) -> {
                BusinessException fail = new BusinessException(ErrorCode.UNAUTHORIZED) {
                };
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                String json = new ObjectMapper().writeValueAsString(Response.error(
                        fail.getErrorCode().getHttpStatus().value(),
                        fail.getMessage(),
                        Map.of(
                                "errorCode", fail.getErrorCode().name()
                        )));
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                PrintWriter writer = response.getWriter();
                writer.write(json);
                writer.flush();
            };

    private final AccessDeniedHandler accessDeniedHandler =
            (request, response, accessDeniedException) -> {
                BusinessException fail = new BusinessException(ErrorCode.FORBIDDEN) {
                };
                response.setStatus(HttpStatus.FORBIDDEN.value());
                String json = new ObjectMapper().writeValueAsString(Response.error(
                        fail.getErrorCode().getHttpStatus().value(),
                        fail.getMessage(),
                        Map.of(
                                "errorCode", fail.getErrorCode().name()
                        )));
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                PrintWriter writer = response.getWriter();
                writer.write(json);
                writer.flush();
            };

    private static final String[] AUTH_URIS = {
            "/api/v1/auth/**"
    };

    private static final String[] SWAGGER_URIS = {
            "/api-docs/**",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    private static final String[] WEBSOCKET_URIS = {
            "/ws/**",
            "/ws/chat/**"
    };

    private static final String[] LOCATION_URIS = {
            "/api/v1/locations/**"
    };

    private static final String[] COUNTRY_URIS = {
            "/api/v1/countries/**"
    };

    private static final String[] CHATROOM_API_URIS = {
            "/api/v1/chatrooms/**"
    };


    public SecurityConfig(
            JwtTokenProvider jwtTokenProvider,
            @Value("${cors.allow-origin-hosts}")
            List<String> allowOriginHosts
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.allowOriginHosts = allowOriginHosts;
    }

    /**
     * Spring Security 필터 체인 설정
     * 
     * 현재는 개발 편의성을 위해 모든 요청을 허용하도록 설정되어 있습니다.
     * 
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain 보안 필터 체인
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (개발 환경용)
                // TODO: 소셜 로그인 구현 시 CSRF 보호 활성화 필요
                .csrf(AbstractHttpConfigurer::disable)

                // 모든 HTTP 요청에 대한 인증/인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, AUTH_URIS).permitAll()
                        // Swagger 문서 접근 허용
                        .requestMatchers(SWAGGER_URIS).permitAll()
                        // WebSocket 엔드포인트 허용
                        .requestMatchers(WEBSOCKET_URIS).permitAll()
                        // 채팅방 API 허용
                        .requestMatchers(CHATROOM_API_URIS).permitAll()
                        .requestMatchers(LOCATION_URIS).permitAll()
                        .requestMatchers(COUNTRY_URIS).permitAll()
                        // 모든 요청을 인증 없이 허용 (개발 환경용)
                        // TODO: API별 세분화된 권한 설정 필요
                        .anyRequest()
                        .authenticated()
                )
                .exceptionHandling((exceptionHandling) ->
                        exceptionHandling.accessDeniedHandler(accessDeniedHandler)
                                .authenticationEntryPoint(unauthorizedEntryPoint)
                )
                .addFilterBefore(new JwtFilter(this.jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                        )
                .cors((cors) -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(this.allowOriginHosts);
                    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "set-cookie", "X-Requested-With"));
                    configuration.setAllowCredentials(true);
                    configuration.setMaxAge(3600L); // WebSocket을 위한 preflight 캐시 시간 설정

                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", configuration);

                    cors.configurationSource(source);
                })
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }
}