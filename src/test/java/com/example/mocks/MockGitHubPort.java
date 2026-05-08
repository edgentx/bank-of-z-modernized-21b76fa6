package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of GitHubPort for testing.
 * Simulates issue creation without calling GitHub API.
 */
public class MockGitHubPort implements GitHubPort {

    private final List<String> createdIssues = new ArrayList<>();
    private String mockUrlPrefix = "https://github.com/mocked-repo/issues/";
    private int issueCounter = 1;

    @Override
    public String createIssue(String title, String body) {
        String mockUrl = mockUrlPrefix + issueCounter++;
        createdIssues.add(mockUrl);
        return mockUrl;
    }

    public List<String> getCreatedIssues() {
        return createdIssues;
    }
    
    public void clear() {
        createdIssues.clear();
        issueCounter = 1;
    }
}
