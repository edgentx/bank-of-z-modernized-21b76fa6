package com.example;

import com.example.adapters.NotificationRepositoryAdapter;
import com.example.adapters.ValidationRepositoryAdapter;
import com.example.adapters.ValidationService;
import com.example.domain.notification.repository.NotificationRepository;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.mocks.MockGitHubPort;
import com.example.mocks.MockNotificationPort;
import com.example.ports.GitHubPort;
import com.example.ports.NotificationPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public GitHubPort gitHubPort() {
        return new MockGitHubPort();
    }

    @Bean
    public NotificationPort notificationPort() {
        return new MockNotificationPort();
    }

    @Bean
    public ValidationRepository validationRepository() {
        return new ValidationRepositoryAdapter();
    }

    @Bean
    public NotificationRepository notificationRepository() {
        return new NotificationRepositoryAdapter();
    }

    @Bean
    public ValidationService validationService(ValidationRepository vRepo, NotificationRepository nRepo, GitHubPort gh, NotificationPort np) {
        return new ValidationService(vRepo, nRepo, gh, np);
    }
}
