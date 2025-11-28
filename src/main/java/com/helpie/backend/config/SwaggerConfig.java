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
                                "JWT 토큰이 필요한 API는 우측 상단 'Authorize' 버튼을 클릭하여 토큰을 입력하세요.\n\n" +
                                "## 최적화된 실시간 채팅 시스템\n\n" +
                                "### WebSocket 연결 (JWT 인증 필요)\n" +
                                "- **연결 URL**: `/ws/chat` (SockJS 지원)\n" +
                                "- **인증**: `Authorization: Bearer {JWT_TOKEN}` 헤더 필수\n" +
                                "- **메시지 전송**: `/app/chat/{chatRoomId}`\n" +
                                "- **입장 알림**: `/app/chat/{chatRoomId}/join`\n" +
                                "- **퇴장 알림**: `/app/chat/{chatRoomId}/leave` (알림 메시지 없음)\n" +
                                "- **메시지 수신**: `/topic/chatroom/{chatRoomId}` 구독\n\n" +
                                "### 성능 최적화 기능\n" +
                                "- **메시지 배치 처리**: 최대 10개 메시지 배치 전송 (100ms 간격)\n" +
                                "- **메시지 압축**: 500바이트 이상 메시지 GZIP 압축\n" +
                                "- **비동기 처리**: 전용 스레드 풀로 응답성 향상\n" +
                                "- **세션 관리**: 중복 연결 방지 및 자동 정리\n\n" +
                                "### 보안 기능\n" +
                                "- **JWT 기반 인증**: WebSocket 연결 시 토큰 검증\n" +
                                "- **소모임 권한 검증**: 멤버만 채팅방 접근 가능\n" +
                                "- **실시간 권한 확인**: 모든 메시지 전송 시 권한 재검증\n\n" +
                                "### 모바일 최적화\n" +
                                "- 메시지 페이징 조회 (무한 스크롤 지원)\n" +
                                "- 온라인 사용자 수 기반 전송 최적화\n" +
                                "- 자동 입장 시스템 메시지 (퇴장 메시지는 UX 개선을 위해 비활성화)")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Helpie Backend Team")
                                .email("jeonwooseon@naver.com")))
                .servers(List.of(
                        new Server()
                                .url("https://helpie.duckdns.org")
                                .description("운영 서버"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("로컬 개발 서버")
                ))
                .addSecurityItem(securityRequirement)
                .components(new Components()
                        .addSecuritySchemes(jwtSchemeName, securityScheme));
    }
}