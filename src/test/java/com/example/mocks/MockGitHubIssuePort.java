package com.example.mocks;

import com.example.ports.GitHubIssuePort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Returns a predictable URL and captures inputs for verification.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private final String mockedUrl;
    private final List<IssueRequest> requests = new ArrayList<>();

    public record IssueRequest(String title, String body) {}

    public MockGitHubIssuePort(String mockedUrl) {
        this.mockedUrl = mockedUrl;
    }

    @Override
    public String createIssue(String title, String body) {
        requests.add(new IssueRequest(title, body));
        return mockedUrl;
    }

    public List<IssueRequest> getRequests() {
        return requests;
    }

    public boolean hasCreatedIssue() {
        return !requests.isEmpty();
    }
}
