package com.example.mocks;

import com.example.domain.defect.ports.GitHubIssueTracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of GitHubIssueTracker for testing.
 * Generates deterministic fake URLs without calling GitHub API.
 */
public class MockGitHubIssueTracker implements GitHubIssueTracker {
    
    public final List<IssueRequest> requests = new ArrayList<>();
    private String fakeBaseUrl = "https://github.com/fake-project/issues/";
    private int sequence = 1;
    private boolean shouldFail = false;

    @Override
    public String createIssue(String title, String body) {
        if (shouldFail) {
            throw new GitHubException("Mock GitHub failure");
        }
        requests.add(new IssueRequest(title, body));
        return fakeBaseUrl + sequence++;
    }

    public void reset() {
        requests.clear();
        sequence = 1;
        shouldFail = false;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    public record IssueRequest(String title, String body) {}
}
