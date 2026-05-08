package com.example.mocks;

import com.example.ports.GitHubIssueTracker;

import java.net.URI;
import java.util.UUID;

/**
 * Mock Adapter for GitHub Issue Tracking.
 * Returns deterministic data for testing.
 */
public class MockGitHubIssueTracker implements GitHubIssueTracker {

    private URI lastCreatedUrl;

    @Override
    public GitHubIssueResponse createIssue(String repoUrl, String title, String body) {
        // Simulate a GitHub API response with a deterministic fake URL based on input
        String fakeId = UUID.randomUUID().toString();
        // Construct a URL that looks real but contains our test data for easy assertion
        this.lastCreatedUrl = URI.create(repoUrl + "/issues/" + fakeId);
        
        return new GitHubIssueResponse(lastCreatedUrl, "open", fakeId);
    }

    public URI getCreatedIssueUrl() {
        return lastCreatedUrl;
    }
}
