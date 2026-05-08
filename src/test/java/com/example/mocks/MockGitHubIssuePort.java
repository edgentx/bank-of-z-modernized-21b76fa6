package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * In-memory mock for GitHub interactions.
 * Allows tests to control the returned URL without a real API call.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String simulatedUrl;

    public void setUrl(String url) {
        this.simulatedUrl = url;
    }

    @Override
    public String getIssueUrl(String issueId) {
        if (simulatedUrl != null) {
            return simulatedUrl;
        }
        // Default fallback if not set, or throw if strictness preferred
        return "https://github.com/default/issues/" + issueId;
    }
}
