package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import com.example.ports.dto.IssueRequest;
import com.example.ports.dto.IssueResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

/**
 * Real implementation for GitHub Issue creation.
 * Uses WebClient to make HTTP calls to GitHub API.
 */
@Component
public class GitHubIssueAdapter implements GitHubIssuePort {

    private final WebClient webClient;

    // In a real environment, this would come from application.properties
    private static final String GITHUB_API_URL = "https://api.github.com/repos/bank-of-z/vforce360/issues";
    private static final String AUTH_TOKEN = "mock-token"; // Placeholder for Injection

    public GitHubIssueAdapter(WebClient.Builder webClientBuilder) {
        // Initialize WebClient with base URL and default headers
        this.webClient = webClientBuilder
            .baseUrl(GITHUB_API_URL)
            .defaultHeader("Authorization", "token " + AUTH_TOKEN)
            .defaultHeader("Accept", "application/vnd.github.v3+json")
            .build();
    }

    @Override
    public CompletableFuture<IssueResponse> createIssue(IssueRequest request) {
        // Create the payload for GitHub API
        GitHubIssuePayload payload = new GitHubIssuePayload(request.title(), request.body());

        // Execute POST request asynchronously
        return webClient.post()
            .bodyValue(payload)
            .retrieve()
            .bodyToMono(GitHubIssueResponse.class)
            .toFuture()
            .thenApply(response -> new IssueResponse(response.htmlUrl(), response.number() + ""));
    }

    // Internal DTOs for JSON mapping
    private record GitHubIssuePayload(String title, String body) {}
    
    private record GitHubIssueResponse(
        int number, 
        String state, 
        String htmlUrl
    ) {}
}
