package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Returns predefined URLs for issue IDs.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private final Map<String, String> issueUrlMap = new HashMap<>();
    private String defaultBaseUrl = "https://github.com/example/issues/";

    @Override
    public String getIssueUrl(String issueId) {
        if (issueUrlMap.containsKey(issueId)) {
            return issueUrlMap.get(issueId);
        }
        // Default behavior for unmapped IDs
        return defaultBaseUrl + issueId;
    }

    public void mockUrl(String issueId, String url) {
        issueUrlMap.put(issueId, url);
    }
}
