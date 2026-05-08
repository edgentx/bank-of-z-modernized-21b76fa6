package com.example.mocks;

import com.example.ports.GitHubIssuePort;

import java.util.Optional;

/**
 * Mock implementation of the GitHub port for testing.
 * Returns predictable URLs without calling the external GitHub API.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private String simulatedBaseUrl = "https://github.com/test/repo/issues/";
    private int issueCount = 0;

    @Override
    public String createIssue(String title, String body) {
        issueCount++;
        return simulatedBaseUrl + issueCount;
    }

    @Override
    public Optional<String> getIssueUrl(String issueId) {
        return Optional.of(simulatedBaseUrl + issueId);
    }

    public void setSimulatedBaseUrl(String url) {
        this.simulatedBaseUrl = url;
    }
}
