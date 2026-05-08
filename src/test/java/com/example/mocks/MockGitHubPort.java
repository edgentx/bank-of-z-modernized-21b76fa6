package com.example.mocks;

import com.example.ports.GitHubPort;

public class MockGitHubPort implements GitHubPort {
    private final String baseUrl = "https://github.com/bank-of-z/issues/issues/";

    @Override
    public String createIssueUrl(String issueId) {
        return baseUrl + issueId;
    }
}
