package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubClient implements GitHubPort {

    private final Map<String, String> issueUrls = new HashMap<>();

    public MockGitHubClient() {
        // Default stub data
        issueUrls.put("VW-454", "https://github.com/egdcrypto/bank-of-z-modernized/issues/454");
    }

    @Override
    public String getIssueUrl(String issueId) {
        return issueUrls.getOrDefault(issueId, "https://github.com/egdcrypto/bank-of-z-modernized/issues/unknown");
    }

    public void stubIssueUrl(String issueId, String url) {
        issueUrls.put(issueId, url);
    }
}
