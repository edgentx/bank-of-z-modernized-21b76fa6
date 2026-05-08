package com.example;

import com.example.adapters.MongoValidationRepositoryAdapter;
import com.example.adapters.TemporalWorkerAdapter;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.ports.SlackNotificationPort;
import com.example.ports.TemporalWorkerPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main Spring Boot Application.
 * Configures the Dependency Injection context for the Adapters and Ports.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Configures the Real Temporal Worker Adapter.
     * Injects the Validation Repository and Slack Notification Port.
     */
    @Bean
    public TemporalWorkerPort temporalWorkerPort(
            ValidationRepository validationRepository,
            SlackNotificationPort slackNotificationPort) {
        return new TemporalWorkerAdapter(validationRepository, slackNotificationPort);
    }

    /**
     * Configures the Real Validation Repository.
     */
    @Bean
    public ValidationRepository validationRepository() {
        return new MongoValidationRepositoryAdapter();
    }

}
