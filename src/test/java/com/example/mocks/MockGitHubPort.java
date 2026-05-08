package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Mock adapter for GitHub API.
 * In-memory implementation to simulate GitHub behavior during testing.
 */
public class MockGitHubPort implements GitHubPort {

    private final Map<String, String> issueUrls = new HashMap<>();

    @Override
    public String createIssue(String title, String body) {
        // Simulate GitHub URL generation
        // Normalize title to key to simulate finding it later
        String url = "https://github.com/fake-org/repo/issues/" + title.hashCode();
        issueUrls.put(title, url);
        return url;
    }

    @Override
    public Optional<String> findIssueUrlByTitle(String title) {
        return Optional.ofNullable(issueUrls.get(title));
    }
}