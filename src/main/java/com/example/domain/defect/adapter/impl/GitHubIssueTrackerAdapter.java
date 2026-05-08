package com.example.domain.defect.adapter.impl;

import com.example.domain.defect.port.GitHubIssueTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Real-world adapter for creating GitHub issues.
 * Uses Spring Boot's RestClient (auto-configured) to call GitHub API.
 * <p>
 * Active only when 'app.feature.github.enabled' is true to prevent
 * execution in environments without credentials or network access.
 */
@Component
@ConditionalOnProperty(name = "app.feature.github.enabled", havingValue = "true", matchIfMissing = false)
public class GitHubIssueTrackerAdapter implements GitHubIssueTracker {

    private static final Logger logger = LoggerFactory.getLogger(GitHubIssueTrackerAdapter.class);
    private final RestClient restClient;
    private final String repoApiUrl;

    public GitHubIssueTrackerAdapter(RestClient.Builder restClientBuilder,
                                     GitHubProperties properties) {
        // Construct the base API URL for the configured repository
        this.repoApiUrl = properties.apiUrl() + "/repos/" + properties.owner() + "/" + properties.repo() + "/issues";
        
        this.restClient = restClientBuilder
                .baseUrl(repoApiUrl)
                .defaultHeader("Authorization", "Bearer " + properties.token())
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .build();
        
        logger.info("Initialized GitHubIssueTrackerAdapter for repo: {}", properties.owner() + "/" + properties.repo());
    }

    @Override
    public IssueDetails createIssue(String title, String description) {
        logger.info("Creating GitHub issue: {}", title);
        
        // Request body matching GitHub API expectations
        // We use a simple record for JSON serialization
        IssueRequest request = new IssueRequest(title, description);

        try {
            IssueResponse response = restClient.post()
                    .body(request)
                    .retrieve()
                    .body(IssueResponse.class);

            if (response != null) {
                logger.info("GitHub issue created successfully: {}", response.htmlUrl());
                return new IssueDetails(String.valueOf(response.number()), response.htmlUrl());
            } else {
                throw new IllegalStateException("GitHub API returned null response");
            }
        } catch (Exception e) {
            logger.error("Failed to create GitHub issue", e);
            // In a real-world batch system, we might throw a recoverable exception here.
            // For this defect fix, we wrap it to satisfy the interface contract.
            throw new RuntimeException("Failed to create GitHub issue: " + e.getMessage(), e);
        }
    }

    // DTOs for JSON serialization/deserialization
    private record IssueRequest(String title, String body) {}

    private record IssueResponse(int number, String htmlUrl) {}

    /**
     * Configuration properties to map 'app.github.*' from application.properties/yaml.
     */
    public record GitHubProperties(String apiUrl, String token, String owner, String repo) {}
}
