package com.helpie.backend.controller.webhook;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DiscordWebhookController {

    /**
     * 디스코드 웹훅 로그 전송 테스트
     * ERROR 로그 발생 시 logback-spring.xml 설정에 따라 Discord로 전송됨
     */
    @GetMapping("/discord-test")
    public String sendDiscordTest() {
        log.error("🚨 디스코드 로그 테스트: RuntimeException 발생", new RuntimeException("테스트 예외"));
        return "Discord Webhook test sent!";
    }
}