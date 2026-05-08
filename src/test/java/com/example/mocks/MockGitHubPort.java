package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of GitHubPort for testing.
 * Simulates GitHub API calls without network I/O.
 */
public class MockGitHubPort implements GitHubPort {

    private final List<String> createdIssues = new ArrayList<>();
    private String mockUrlPrefix = "https://github.com/mock-repo/issues/";
    private int counter = 1;

    @Override
    public String createIssue(String title, String body) {
        String url = mockUrlPrefix + counter++;
        createdIssues.add(url);
        // Simulate the behavior of returning a real URL
        return url;
    }

    public List<String> getCreatedIssues() {
        return new ArrayList<>(createdIssues);
    }
}