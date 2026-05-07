package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mock adapter for GitHub operations.
 * Simulates creating an issue and returning a deterministic URL.
 */
@Component
public class MockGitHubIssuePort implements GitHubIssuePort {

    @Override
    public String createIssue(String title, String body) {
        // Simulate GitHub API latency or logic if necessary.
        // Return a deterministic URL based on a generated ID.
        String issueId = UUID.randomUUID().toString();
        return "https://github.com/example/repo/issues/" + issueId;
    }
}
