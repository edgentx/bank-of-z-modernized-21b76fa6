package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns predictable URLs based on issue IDs.
 */
public class MockGitHubPort implements GitHubPort {

    private final Map<String, String> issueMap = new HashMap<>();
    private final String baseUrl;

    public MockGitHubPort() {
        this.baseUrl = "https://github.com/egdcrypto/bank-of-z/issues";
    }

    @Override
    public String getIssueUrl(String issueId) {
        if (issueMap.containsKey(issueId)) {
            return issueMap.get(issueId);
        }
        // Default behavior: construct URL
        return baseUrl + "/" + issueId;
    }

    /**
     * Allows tests to override the URL for a specific issue.
     */
    public void mockUrl(String issueId, String url) {
        issueMap.put(issueId, url);
    }
}
