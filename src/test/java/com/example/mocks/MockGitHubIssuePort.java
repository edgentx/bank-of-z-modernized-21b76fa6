package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of the GitHub Port.
 * Allows controlling responses for testing without hitting real GitHub API.
 */
@Component
public class MockGitHubIssuePort implements GitHubIssuePort {

    @Override
    public GitHubIssueResponse createIssue(String title, String body, String labels) {
        // Default mock behavior: Return a dummy URL.
        // In a real test setup, this can be overridden via a test config or Mockito spies,
        // but for the purpose of the requested Mock Adapter pattern, we return a valid static object.
        return new GitHubIssueResponse("https://github.com/bank-of-z/vforce360/issues/MOCK-123");
    }
}
