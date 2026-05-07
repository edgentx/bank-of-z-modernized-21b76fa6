package com.example.config;

import com.example.adapters.RestTemplateGitHubAdapter;
import com.example.adapters.WebClientSlackAdapter;
import com.example.domain.vforce.DefectReporter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackNotificationPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for the VForce360 domain components.
 * Binds the real adapters to the ports.
 */
@Configuration
public class VForceConfig {

    @Bean
    public DefectReporter defectReporter(
            SlackNotificationPort slackNotificationPort,
            GitHubPort gitHubPort
    ) {
        return new DefectReporter(slackNotificationPort, gitHubPort);
    }

    // Adapter Beans - Spring Boot auto-configuration usually handles RestTemplate/WebClient builders,
    // but we explicitly construct the Adapters here to inject ports.
    
    // The Adapters themselves are annotated with @Component, so they are picked up by scanning.
    // However, we define the builders here if we need specific tuning.
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
