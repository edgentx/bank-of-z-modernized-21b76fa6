package com.example.config;

import com.example.adapters.WebClientSlackAdapter;
import com.example.ports.SlackPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration for Dependency Injection.
 * Uses the adapter pattern to provide concrete implementations for ports.
 */
@Configuration
public class SpringConfig {

    @Bean
    @ConditionalOnMissingBean
    public SlackPort slackPort() {
        // In a real environment, this might pick between a Mock or Real adapter based on profile.
        // For the defect fix, we provide the real WebClient adapter.
        return new WebClientSlackAdapter();
    }
}
