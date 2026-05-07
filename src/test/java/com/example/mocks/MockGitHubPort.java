package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 * Returns predictable URLs.
 */
public class MockGitHubPort implements GitHubPort {

    private String mockBaseUrl = "https://github.com/mock-org/repo/issues/";
    private int issueCounter = 1;

    @Override
    public String createIssue(String title, String body) {
        String url = mockBaseUrl + issueCounter;
        issueCounter++;
        return url;
    }
}
