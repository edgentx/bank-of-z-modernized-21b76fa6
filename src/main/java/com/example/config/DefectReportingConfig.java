package com.example.config;

import com.example.adapters.NotificationPortImpl;
import com.example.adapters.GitHubPortImpl;
import com.example.adapters.ValidationRepositoryImpl;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.ports.GitHubPort;
import com.example.ports.NotificationPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;

/**
 * Spring Configuration for Defect Reporting components.
 * Wires the adapters to the ports.
 */
@Configuration
public class DefectReportingConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public NotificationPort notificationPort(
            @Value("${defects.slack.webhook.url}") String webhookUrl,
            RestTemplate restTemplate) {
        return new NotificationPortImpl(webhookUrl, restTemplate);
    }

    @Bean
    public GitHubPort gitHubPort(
            @Value("${defects.github.api.url}") String apiUrl,
            @Value("${defects.github.token}") String token,
            RestTemplate restTemplate) {
        return new GitHubPortImpl(apiUrl, token, restTemplate);
    }

    @Bean
    public ValidationRepository validationRepository() {
        // In a real scenario, this would be a JPA/Repository implementation.
        // For this feature, we satisfy the interface with the adapter.
        return new ValidationRepositoryImpl();
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
