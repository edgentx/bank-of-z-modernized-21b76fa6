package com.example.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnProperty(name = "app.github.enabled", havingValue = "true", matchIfMissing = false)
public class GitHubConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}