package com.example.mocks;

import com.example.ports.GitHubIssueTrackerPort;
import java.util.UUID;

/**
 * Mock implementation of GitHubIssueTrackerPort for testing.
 * Simulates issue creation without network calls.
 */
public class MockGitHubIssueTrackerAdapter implements GitHubIssueTrackerPort {

    private String nextMockUrl = "https://github.com/mock-org/repo/issues/1";
    private int callCount = 0;

    @Override
    public String createIssue(String title, String body) {
        callCount++;
        // In a real scenario, this would return the URL from the API response.
        // For the red phase test, we just return a predictable string.
        return "https://github.com/example/bank/issues/" + callCount;
    }

    public void setNextMockUrl(String url) {
        this.nextMockUrl = url;
    }

    public int getCallCount() {
        return callCount;
    }
}
