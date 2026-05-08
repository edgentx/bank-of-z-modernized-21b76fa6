package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Fake implementation of GitHubPort for testing.
 * Returns a predictable URL without network calls.
 */
public class FakeGitHubPort implements GitHubPort {
    private final String fakeBaseUrl;

    public FakeGitHubPort() {
        this("https://github.com/fake-issue/");
    }

    public FakeGitHubPort(String fakeBaseUrl) {
        this.fakeBaseUrl = fakeBaseUrl;
    }

    @Override
    public String createIssue(String title, String body) {
        // Simulate GitHub returning a new issue URL
        return fakeBaseUrl + System.currentTimeMillis();
    }
}
