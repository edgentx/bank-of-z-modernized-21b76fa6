package com.example.infrastructure.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Configuration for GitHub Feign client.
 * Handles OAuth2 token injection for API authentication.
 */
public class GitHubFeignConfig {

    @Value("${github.token}")
    private String githubToken;

    @Bean
    public RequestInterceptor authRequestInterceptor() {
        return template -> {
            if (githubToken != null && !githubToken.isBlank()) {
                template.header("Authorization", "Bearer " + githubToken);
            }
            template.header("Accept", "application/vnd.github+json");
            template.header("X-GitHub-Api-Version", "2022-11-28");
        };
    }
}
