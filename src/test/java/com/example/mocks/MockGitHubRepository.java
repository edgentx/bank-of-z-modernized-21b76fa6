package com.example.mocks;

import com.example.domain.ports.GitHubRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock adapter for GitHub Repository.
 * Allows tests to control the URL returned without real HTTP calls.
 */
public class MockGitHubRepository implements GitHubRepository {

    private String nextIssueUrl;
    private final List<String> calls = new ArrayList<>();

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }

    @Override
    public String createIssue(String title, String description) {
        // Record that this method was called
        this.calls.add(title);
        
        // If the test didn't set a URL, return a default valid one
        if (nextIssueUrl == null) {
            return "https://github.com/default-repo/issues/1";
        }
        return nextIssueUrl;
    }

    public boolean wasCalledWith(String title) {
        return calls.contains(title);
    }
}
