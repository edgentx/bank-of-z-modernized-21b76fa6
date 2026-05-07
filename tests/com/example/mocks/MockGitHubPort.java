package com.example.mocks;

import com.example.ports.GitHubPort;
import com.example.vforce.github.IssueLink;
import com.example.vforce.shared.ReportDefectCommand;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns predictable URLs without calling GitHub API.
 */
public class MockGitHubPort implements GitHubPort {
    private final IssueLink mockLink;
    private int createCallCount = 0;

    public MockGitHubPort(String mockUrl) {
        this.mockLink = new IssueLink(mockUrl);
    }

    @Override
    public IssueLink createIssue(ReportDefectCommand command) {
        // Simulate HTTP latency
        createCallCount++;
        return mockLink;
    }

    public int getCreateCallCount() {
        return createCallCount;
    }
}