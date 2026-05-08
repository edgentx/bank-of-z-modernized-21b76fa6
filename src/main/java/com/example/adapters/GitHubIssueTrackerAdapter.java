package com.example.adapters;

import com.example.ports.GitHubIssueTrackerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Real-world implementation of GitHubIssueTrackerPort using WebClient.
 * Interacts with GitHub REST API to create issues.
 */
@Component
public class GitHubIssueTrackerAdapter implements GitHubIssueTrackerPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubIssueTrackerAdapter.class);
    private static final String GITHUB_API_BASE = "https://api.github.com";

    private final WebClient webClient;
    private final String authToken;
    private final String repoOwner;
    private final String repoName;

    /**
     * Constructs the adapter.
     *
     * @param webClientBuilder The WebClient builder (injected by Spring)
     * @param authToken        GitHub Personal Access Token (Classic or Fine-grained)
     * @param repoOwner        Organization or User owner (e.g., "example-org")
     * @param repoName         Repository name (e.g., "bank-of-z")
     */
    public GitHubIssueTrackerAdapter(WebClient.Builder webClientBuilder, String authToken, String repoOwner, String repoName) {
        this.authToken = authToken;
        this.repoOwner = repoOwner;
        this.repoName = repoName;
        this.webClient = webClientBuilder
                .baseUrl(GITHUB_API_BASE)
                .build();
    }

    @Override
    public String createIssue(String title, String body) {
        try {
            String uri = String.format("/repos/%s/%s/issues", repoOwner, repoName);

n            GitHubIssueResponse response = webClient.post()
                    .uri(uri)
                    .headers(h -> {
                        h.setBearerAuth(authToken);
                        h.set("Accept", "application/vnd.github+json");
                        h.set("X-GitHub-Api-Version", "2022-11-28");
                    })
                    .bodyValue(new GitHubIssueRequest(title, body))
                    .retrieve()
                    .bodyToMono(GitHubIssueResponse.class)
                    .block(); // Block for synchronous execution

            if (response != null && response.htmlUrl() != null) {
                log.info("Created GitHub issue {}: {}", response.number(), response.htmlUrl());
                return response.htmlUrl();
            } else {
                throw new RuntimeException("GitHub API returned empty response");
            }

        } catch (WebClientResponseException e) {
            log.error("Failed to create GitHub issue: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to create GitHub issue", e);
        } catch (Exception e) {
            log.error("Error creating GitHub issue", e);
            throw new RuntimeException("Error creating GitHub issue", e);
        }
    }

    /**
     * DTO for GitHub API Create Issue request.
     */
    private record GitHubIssueRequest(String title, String body) {}

    /**
     * DTO for GitHub API Create Issue response.
     */
    private record GitHubIssueResponse(int number, String htmlUrl) {}
}
