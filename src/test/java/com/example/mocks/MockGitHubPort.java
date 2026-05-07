package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.Optional;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns predictable URLs without hitting the network.
 */
public class MockGitHubPort implements GitHubPort {

    private String lastCreatedIssueTitle;
    private String lastCreatedIssueDescription;

    @Override
    public String createIssue(String repoOwner, String repoName, String title, String description) {
        this.lastCreatedIssueTitle = title;
        this.lastCreatedIssueDescription = description;
        // Simulate GitHub returning a URL
        return "https://github.com/" + repoOwner + "/" + repoName + "/issues/1";
    }

    @Override
    public Optional<String> getIssueUrl(String issueId) {
        return Optional.of("https://github.com/vforce360/shared-infra/issues/" + issueId);
    }

    public String getLastCreatedIssueTitle() {
        return lastCreatedIssueTitle;
    }
    
    public String getLastCreatedIssueDescription() {
        return lastCreatedIssueDescription;
    }
}
