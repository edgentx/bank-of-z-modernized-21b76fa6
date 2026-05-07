package com.example.mocks;

import com.example.ports.GitHubIssuePort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubIssuePort.
 * Returns predictable URLs without calling the real GitHub API.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private final Map<String, String> issues = new HashMap<>();
    private final String baseUrl;

    public MockGitHubIssuePort(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String createIssue(String title, String description) {
        // Simulate GitHub issue creation logic
        String id = String.valueOf(Math.abs(title.hashCode())); // Pseudo-ID generation
        String url = baseUrl + "/" + id;
        issues.put(title, url);
        return url;
    }
}
