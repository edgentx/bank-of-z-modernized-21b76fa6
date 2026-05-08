package com.example.adapters;

import com.example.domain.validation.model.ReportDefectCommand;
import com.example.ports.GitHubIssueTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Real implementation for GitHub Issue Tracker.
 * Uses Spring Boot's RestClient to interact with GitHub API.
 */
@Component
public class GitHubRestAdapter implements GitHubIssueTracker {

    private static final Logger log = LoggerFactory.getLogger(GitHubRestAdapter.class);

    private final RestClient restClient;
    private final String apiBaseUrl;
    private final String repoOwner;
    private final String repoName;
    private final String authToken;

    public GitHubRestAdapter(
            @Value("${github.api.base-url:https://api.github.com}") String apiBaseUrl,
            @Value("${github.repo.owner:bank-of-z}") String repoOwner,
            @Value("${github.repo.name:project}") String repoName,
            @Value("${github.auth.token}") String authToken,
            RestClient.Builder restClientBuilder) {
        this.apiBaseUrl = apiBaseUrl;
        this.repoOwner = repoOwner;
        this.repoName = repoName;
        this.authToken = authToken;
        this.restClient = restClientBuilder
            .defaultHeader("Authorization", "Bearer " + authToken)
            .defaultHeader("Accept", "application/vnd.github+json")
            .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
            .build();
    }

    @Override
    public String createIssue(ReportDefectCommand cmd) {
        if (authToken == null || authToken.isBlank()) {
            log.warn("GitHub Auth Token is missing. Cannot create issue. Returning null URL.");
            // Returning null triggers the IllegalStateException in the handler as per requirements
            return null;
        }

        try {
            String url = String.format("%s/repos/%s/%s/issues", apiBaseUrl, repoOwner, repoName);
            
            // Construct Request Body
            // Note: In a real app, use a proper DTO class. Using String format here for brevity/single-file limit.
            String jsonBody = String.format(
                "{\"title\":\"%s\", \"body\":\"%s\"}",
                escapeJson(cmd.title()),
                escapeJson(cmd.description())
            );

            GitHubIssueResponse response = restClient.post()
                .uri(url)
                .body(jsonBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    log.error("GitHub API Error: {}", res.getStatusCode());
                    throw new RuntimeException("GitHub API returned error: " + res.getStatusCode());
                })
                .body(GitHubIssueResponse.class);

            if (response != null && response.htmlUrl() != null) {
                log.info("Created GitHub issue: {}", response.htmlUrl());
                return response.htmlUrl();
            }

        } catch (Exception e) {
            log.error("Failed to create GitHub issue for title: {}", cmd.title(), e);
        }

        return null;
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    /**
     * Internal record to map the GitHub JSON response.
     */
    private record GitHubIssueResponse(String htmlUrl, int id) {}
}
