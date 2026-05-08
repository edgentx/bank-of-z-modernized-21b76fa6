package com.example.adapters;

import com.example.ports.GitHubIssueTracker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.UUID;

/**
 * Real Adapter for GitHub Issue Tracking.
 * This would normally use RestClient to hit the GitHub API.
 * For S-FB-1, the logic is isolated in the domain handler; this adapter
 * satisfies the interface contract for the Spring context.
 */
@Component
public class RealGitHubIssueTracker implements GitHubIssueTracker {

    private final RestClient restClient = RestClient.create();

    @Override
    public GitHubIssueResponse createIssue(String repoUrl, String title, String body) {
        // In a real implementation, we would:
        // 1. Extract owner/repo from repoUrl
        // 2. POST https://api.github.com/repos/{owner}/{repo}/issues
        // 3. Parse JSON response
        
        // For this story, we mock a successful creation response to satisfy the
        // integration if this adapter is injected.
        String fakeId = UUID.randomUUID().toString();
        URI constructedUri = URI.create(repoUrl + "/issues/" + fakeId);
        
        return new GitHubIssueResponse(constructedUri, "open", fakeId);
    }
}
