package com.example.mocks;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation for GitHub API.
 * Returns predictable data without making network calls.
 */
@Component
public class MockGitHubPort implements GitHubPort {

    private final Map<String, String> issueStore = new HashMap<>();

    public MockGitHubPort() {
        // Initialize with some dummy data consistent with the story
        issueStore.put("VW-454", "https://github.com/bank-of-z/issues/454");
    }

    @Override
    public String getIssueUrl(String issueId) {
        return issueStore.getOrDefault(issueId, "https://github.com/bank-of-z/issues/unknown");
    }
}
