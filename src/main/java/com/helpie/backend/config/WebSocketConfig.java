package com.helpie.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 설정
 * STOMP를 사용한 실시간 채팅 구현
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    private final WebSocketAuthInterceptor authInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트로 메시지를 전송할 때 사용할 prefix
        config.enableSimpleBroker("/topic", "/queue")
              .setHeartbeatValue(new long[]{10000, 10000}) // 하트비트 설정 (10초)
              .setTaskScheduler(createTaskScheduler()); // 커스텀 스케줄러 사용
        
        // 클라이언트에서 서버로 메시지를 보낼 때 사용할 prefix
        config.setApplicationDestinationPrefixes("/app");
        
        // 메시지 크기 제한 설정
        config.setCacheLimit(1024); // 캐시 크기 제한
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 연결을 위한 endpoint 설정
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*") // CORS 허용
                .withSockJS(); // SockJS 지원 (WebSocket을 지원하지 않는 브라우저 대응)
    }
    
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 인증 인터셉터 등록
        registration.interceptors(authInterceptor);
        
        // 인바운드 채널 스레드 풀 설정
        registration.taskExecutor()
                   .corePoolSize(5)
                   .maxPoolSize(20)
                   .keepAliveSeconds(60);
    }
    
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        // 아웃바운드 채널 스레드 풀 설정
        registration.taskExecutor()
                   .corePoolSize(5)
                   .maxPoolSize(20)
                   .keepAliveSeconds(60);
    }
    
    /**
     * WebSocket용 커스텀 스케줄러 생성
     */
    private TaskScheduler createTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("WebSocket-Scheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        scheduler.initialize();
        return scheduler;
    }
}