package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.domain.verification.service.VerificationService;
import com.example.ports.SlackNotificationPort;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    static class VerificationConfig {
        
        // Assuming a real SlackNotificationPort bean exists elsewhere or is mocked in tests.
        // For the purpose of the compilation, we inject it via Spring context if available.
        // This ensures the VerificationService is available for Autowiring in tests.
        
        @Bean
        public VerificationService verificationService(SlackNotificationPort slackNotificationPort) {
            return new VerificationService(slackNotificationPort);
        }
    }
}
