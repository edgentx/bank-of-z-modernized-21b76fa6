package com.example.mocks;

import com.example.ports.GithubIssuePort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of GithubIssuePort for testing.
 * Records interactions to verify behavior.
 */
public class MockGithubIssuePort implements GithubIssuePort {

    private final List<String> createdIssues = new ArrayList<>();
    private String mockUrl = "https://github.com/mock/issues/1";

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    @Override
    public String createIssue(String title, String description) {
        // Simulate recording the call
        createdIssues.add(title);
        return mockUrl;
    }

    public boolean wasIssueCreated(String title) {
        return createdIssues.contains(title);
    }
}
