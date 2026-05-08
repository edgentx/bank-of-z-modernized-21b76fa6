package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Real implementation of GitHubPort using Spring RestClient.
 */
public class GitHubAdapter implements GitHubPort {

    private final RestClient restClient;
    private static final String API_URL = "https://api.github.com/repos/bank-of-z/core/issues";

    public GitHubAdapter(RestClient.Builder restClientBuilder) {
        // In a real scenario, authorization tokens and specific config
        // would be injected here.
        this.restClient = restClientBuilder
            .baseUrl(API_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    @Override
    public String createIssue(String title, String body) {
        // Request DTO
        record CreateIssueRequest(String title, String body) {}

        try {
            // Response DTO (extracting HTML_URL from GitHub response)
            // GitHub response: {"html_url": "https://github.com/...", "id": 1, ...}
            record GitHubIssueResponse(String html_url) {}

            ResponseEntity<GitHubIssueResponse> response = restClient.post()
                .body(new CreateIssueRequest(title, body))
                .retrieve()
                .toEntity(GitHubIssueResponse.class);

            if (response.getBody() != null && response.getBody().html_url() != null) {
                return response.getBody().html_url();
            }
            throw new GitHubException("Invalid response from GitHub API", null);

        } catch (Exception e) {
            throw new GitHubException("Failed to create GitHub issue", e);
        }
    }
}
