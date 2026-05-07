package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.HashSet;
import java.util.Set;

/** Mock GitHub Port for testing. */
public class MockGitHubPort implements GitHubPort {
    private final Set<String> createdIssues = new HashSet<>();
    private String simulatedUrlPrefix = "https://github.com/bank-of-z/issues/";

    @Override
    public String createIssue(String title, String body) {
        String id = java.util.UUID.randomUUID().toString().substring(0, 8);
        String url = simulatedUrlPrefix + id;
        createdIssues.add(url);
        return url;
    }

    public boolean wasIssueCreated(String url) {
        return createdIssues.contains(url);
    }
}
