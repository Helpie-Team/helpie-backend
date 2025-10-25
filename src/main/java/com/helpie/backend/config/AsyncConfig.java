package com.helpie.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 처리 설정
 * WebSocket 메시지 전송 성능 최적화를 위한 스레드 풀 설정
 * 
 * @author 전우선
 * @since 2025-10-25(토)
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {
    
    /**
     * WebSocket 메시지 전송용 비동기 스레드 풀
     */
    @Bean(name = "websocketTaskExecutor")
    public Executor websocketTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 기본 스레드 수 (항상 활성 상태로 유지)
        executor.setCorePoolSize(5);
        
        // 최대 스레드 수 (부하 증가 시 확장)
        executor.setMaxPoolSize(20);
        
        // 큐 용량 (대기 중인 작업 수)
        executor.setQueueCapacity(100);
        
        // 스레드 이름 접두사
        executor.setThreadNamePrefix("WebSocket-");
        
        // 스레드 유지 시간 (초)
        executor.setKeepAliveSeconds(60);
        
        // 애플리케이션 종료 시 대기 중인 작업 완료까지 기다림
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 종료 대기 시간 (초)
        executor.setAwaitTerminationSeconds(30);
        
        // 거부된 작업 처리 정책 (호출자 스레드에서 실행)
        executor.setRejectedExecutionHandler((task, executor1) -> {
            log.warn("WebSocket 작업이 거부되었습니다. 호출자 스레드에서 실행합니다.");
            task.run();
        });
        
        executor.initialize();
        
        log.info("WebSocket 비동기 스레드 풀 초기화 완료 - Core: {}, Max: {}, Queue: {}", 
            executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        
        return executor;
    }
    
    /**
     * 일반적인 비동기 작업용 스레드 풀
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("Async-");
        executor.setKeepAliveSeconds(60);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        
        log.info("일반 비동기 스레드 풀 초기화 완료");
        
        return executor;
    }
}