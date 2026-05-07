package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubIssuePort.
 * Simulates creating an issue and returning a predictable URL.
 */
@Component
public class MockGitHubIssuePort implements GitHubIssuePort {

    private final Map<String, String> mockIssues = new HashMap<>();
    private int counter = 1;

    @Override
    public String createIssue(String title, String body) {
        // Simulate GitHub API call returning a URL
        String url = "https://github.com/mock-org/repo/issues/" + (counter++);
        mockIssues.put(url, title);
        return url;
    }

    public int getIssueCount() {
        return mockIssues.size();
    }
}