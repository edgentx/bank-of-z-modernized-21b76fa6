package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation for GitHubIssuePort.
 * Stores created issues in memory to verify behavior without external I/O.
 */
@Component
public class MockGitHubIssuePort implements GitHubIssuePort {

    public static class Issue {
        public final String title;
        public final String description;
        public final String url;

        public Issue(String title, String description, String url) {
            this.title = title;
            this.description = description;
            this.url = url;
        }
    }

    private final List<Issue> createdIssues = new ArrayList<>();
    private int issueCount = 0;

    @Override
    public String createIssue(String title, String description) {
        // Simulate creating an issue and returning a generated URL
        issueCount++;
        String url = "https://github.com/fake-org/fake-repo/issues/" + issueCount;
        this.createdIssues.add(new Issue(title, description, url));
        return url;
    }

    public List<Issue> getCreatedIssues() {
        return new ArrayList<>(createdIssues);
    }

    public void clear() {
        createdIssues.clear();
        issueCount = 0;
    }
}
