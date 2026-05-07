package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Optional;

/**
 * Real implementation of GitHubPort using RestTemplate.
 * Fetches issue details from the GitHub API.
 */
@Component
public class RestTemplateGitHubAdapter implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateGitHubAdapter.class);
    private final RestTemplate restTemplate;
    private final String apiBaseUrl;
    private final String repoOwner;
    private final String repoName;

    public RestTemplateGitHubAdapter(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${github.api.base-url:https://api.github.com}") String apiBaseUrl,
            @Value("${github.repo.owner:example}") String repoOwner,
            @Value("${github.repo.name:dummy-repo}") String repoName
    ) {
        // Configure timeout and basic auth if needed (omitted for brevity, using Bearer usually)
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
        this.apiBaseUrl = apiBaseUrl;
        this.repoOwner = repoOwner;
        this.repoName = repoName;
    }

    @Override
    public Optional<String> getIssueUrl(String issueId) {
        try {
            // Construct API URL: https://api.github.com/repos/{owner}/{repo}/issues/{issueId}
            // Note: issueId often corresponds to a number in GitHub APIs, but here we treat it as a lookup key.
            // Assuming the repo stores standard GitHub issues where number is the ID.
            // We'll fetch the issue object to get the 'html_url'.
            
            String url = String.format("%s/repos/%s/%s/issues/%s", apiBaseUrl, repoOwner, repoName, issueId);
            
            // Simple map response expected: { "html_url": "...", ... }
            // In a real app, we would map to a DTO class (GitHubIssueResponse)
            GitHubIssueResponse response = restTemplate.getForObject(url, GitHubIssueResponse.class);
            
            if (response != null && response.html_url != null) {
                return Optional.of(response.html_url);
            }
            return Optional.empty();

        } catch (Exception e) {
            log.warn("Failed to retrieve GitHub URL for issue {}: {}", issueId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> createIssueAndReturnUrl(String title, String body) {
        // Implementation for POST /issues
        return Optional.empty();
    }

    /**
     * Inner DTO for deserialization.
     * Kept internal to the adapter to avoid polluting domain.
     */
    private static class GitHubIssueResponse {
        public String html_url;
        public String state;
        public String title;
    }
}
