package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import com.example.domain.validation.port.ValidationRepository;
import com.example.domain.validation.port.GitHubPort;
import com.example.domain.validation.port.SlackPort;
import com.example.infrastructure.adapters.JpaValidationRepositoryAdapter;
import com.example.infrastructure.adapters.GitHubFeignAdapter;
import com.example.infrastructure.adapters.SlackWebhookAdapter;

/**
 * Main Spring Boot Application entry point.
 * Configures the real-world adapters for the domain ports.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.domain", "com.example.infrastructure", "com.example.api"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.java, args);
    }

    // Wiring the domain to infrastructure
    // In a real scenario, we might use profiles (dev/prod) to switch implementations.

    @Bean
    public ValidationRepository validationRepository(JpaValidationRepositoryAdapter adapter) {
        return adapter;
    }

    @Bean
    public GitHubPort gitHubPort(GitHubFeignAdapter adapter) {
        return adapter;
    }

    @Bean
    public SlackPort slackPort(SlackWebhookAdapter adapter) {
        return adapter;
    }
}
