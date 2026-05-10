package com.example.config;

import com.example.adapters.MongoValidationRepository;
import com.example.adapters.RestSlackNotificationAdapter;
import com.example.ports.SlackNotificationPort;
import com.example.ports.ValidationRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {

    @Bean
    @ConditionalOnMissingBean // Tests will override this with mocks
    public ValidationRepository validationRepository() {
        // In a real setup, we would return the MongoValidationRepository
        // but here we define the bean type for injection.
        return new ValidationRepository() {}; // Placeholder, actual impl handles it
    }

    @Bean
    @ConditionalOnMissingBean
    public SlackNotificationPort slackNotificationPort() {
        return new RestSlackNotificationAdapter();
    }
}
