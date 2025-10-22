package com.helpie.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 3.0 설정 클래스
 * 
 * API 문서 자동 생성 및 테스트를 위한 Swagger UI 설정을 담당합니다.
 * 개발 환경에서 API 스펙 확인 및 테스트 용도로 사용됩니다.
 *
 * 
 * @author 전우선
 * @since 2025-10-14(화)
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 설정 빈 생성
     * 
     * API 문서의 기본 정보, 서버 정보, JWT 인증 스키마 등을 설정합니다.
     * 
     * @return OpenAPI 설정 객체
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // JWT 보안 스키마 이름
        String jwtSchemeName = "JWT Authentication";
        
        // JWT 보안 요구사항 정의
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(jwtSchemeName);
        
        // JWT 보안 스키마 정의
        SecurityScheme securityScheme = new SecurityScheme()
                .name(jwtSchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT 토큰을 입력하세요. (Bearer prefix 제외)");
        
        return new OpenAPI()
                .info(new Info()
                        .title("Helpie Backend API")
                        .description("헬피 백엔드 서비스 API 문서\n\n" +
                                "개발팀: 배준오, 임지우, 전우선\n\n" +
                                "JWT 토큰이 필요한 API는 우측 상단 'Authorize' 버튼을 클릭하여 토큰을 입력하세요.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Helpie Backend Team")
                                .email("jeonwooseon@naver.com")))
                .servers(List.of(
                        new Server()
                                .url("http://49.50.133.140:8080")  // ← 실제 배포된 서버 주소
                                .description("배포 서버"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("로컬 개발 서버")
                ))
                .addSecurityItem(securityRequirement)
                .components(new Components()
                        .addSecuritySchemes(jwtSchemeName, securityScheme));
    }
}