package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of GitHubPort.
 * Predictable, in-memory behavior for testing. No real HTTP calls.
 */
public class InMemoryGitHubPort implements GitHubPort {

    private String nextIssueUrl;
    private final List<IssueRequest> createdIssues = new ArrayList<>();

    public InMemoryGitHubPort() {
        // Default predictable URL if not overridden
        this.nextIssueUrl = "https://github.com/mock/repo/issues/1";
    }

    /**
     * Sets the URL to be returned by the next createIssue call.
     * This allows tests to predict exactly what the Slack body should contain.
     */
    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }

    @Override
    public String createIssue(String title, String body) {
        // Store request for verification if needed
        createdIssues.add(new IssueRequest(title, body));
        
        if (title == null) {
            throw new IllegalArgumentException("Title cannot be null");
        }

        return nextIssueUrl;
    }

    public List<IssueRequest> getCreatedIssues() {
        return new ArrayList<>(createdIssues);
    }

    public record IssueRequest(String title, String body) {}
}
