package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Allows configuring specific URLs for issue IDs.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private final Map<String, String> urlMap = new HashMap<>();
    private String defaultUrl = null;

    public void mockUrl(String issueId, String url) {
        urlMap.put(issueId, url);
    }

    public void setDefaultUrl(String url) {
        this.defaultUrl = url;
    }

    @Override
    public String getIssueUrl(String issueId) {
        if (urlMap.containsKey(issueId)) {
            return urlMap.get(issueId);
        }
        if (defaultUrl != null) {
            return defaultUrl;
        }
        // Default throw to ensure tests fail if not configured correctly
        throw new RuntimeException("MockGitHubIssuePort: No URL configured for issue " + issueId);
    }
}
