package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of GitHubPort for testing.
 * Records calls and allows verification of issue creation content.
 */
public class MockGitHubPort implements GitHubPort {

    private final List<IssueRequest> createdIssues = new ArrayList<>();
    private String nextIssueUrl = "https://github.com/example/repo/issues/1";

    public record IssueRequest(String title, String body) {}

    @Override
    public String createIssue(String title, String body) {
        createdIssues.add(new IssueRequest(title, body));
        return nextIssueUrl;
    }

    public List<IssueRequest> getCreatedIssues() {
        return new ArrayList<>(createdIssues);
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }

    public void reset() {
        createdIssues.clear();
        nextIssueUrl = "https://github.com/example/repo/issues/1";
    }
}
