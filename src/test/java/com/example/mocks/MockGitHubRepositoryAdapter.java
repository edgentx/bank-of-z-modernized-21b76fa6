package com.example.mocks;

import com.example.ports.GitHubRepositoryPort;

/**
 * Mock adapter for GitHub repository interactions.
 * Simulates issue creation without calling the actual GitHub API.
 */
public class MockGitHubRepositoryAdapter implements GitHubRepositoryPort {

    private int issueSequence = 1;
    private final String mockBaseUrl = "https://github.com/fake-org/vforce360/issues/";

    @Override
    public String createIssue(String title, String body) {
        // Simulate successful creation
        String url = mockBaseUrl + issueSequence++;
        // In a real scenario, we might validate the inputs here if needed,
        // but for the mock adapter, we just return the predictable URL.
        return url;
    }

    public void resetSequence() {
        issueSequence = 1;
    }
}