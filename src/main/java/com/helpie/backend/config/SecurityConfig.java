package com.helpie.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

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
                // 모든 요청을 인증 없이 허용 (개발 환경용)
                // TODO: API별 세분화된 권한 설정 필요
                .anyRequest().permitAll()
            );
        
        return http.build();
    }
}