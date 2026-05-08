package com.example.steps;

import com.example.ports.GitHubIssuePort;

/**
 * Mock adapter for GitHubIssuePort.
 * Returns a predefined URL to simulate successful issue creation.
 */
public class MockGitHubIssueClient implements GitHubIssuePort {

    private String nextIssueUrl = "https://github.com/bank-of-z/issues/1";

    @Override
    public String createIssue(String title, String body) {
        System.out.println("[MockGitHub] Created issue: " + title + " -> " + nextIssueUrl);
        return nextIssueUrl;
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }
}