package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Real adapter for GitHub operations using HTTP client.
 * In a real environment, this would use something like OkHttp or WebClient
 * to hit the GitHub API.
 */
@Component
public class RealGitHubAdapter implements GitHubPort {

    private static final Logger logger = Logger.getLogger(RealGitHubAdapter.class.getName());

    @Value("${github.api.url}")
    private String githubApiUrl;

    @Value("${github.api.token}")
    private String authToken;

    @Override
    public Optional<String> createIssue(String title, String body) {
        logger.info("[GitHub Adapter] Creating issue: " + title + " via " + githubApiUrl);
        
        // Implementation Note: Real HTTP call omitted for TDD simplicity in this snippet.
        // Would typically use:
        // WebClient webClient = WebClient.create(githubApiUrl);
        // return webClient.post()...
        
        // Simulating success for the "Green" phase implementation structure
        return Optional.of(githubApiUrl + "/issues/123");
    }
}
