package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of GitHubPort for testing.
 * Allows setting expectations on the created URL and verifying interactions.
 */
public class MockGitHubClient implements GitHubPort {
    private final List<String> createdTitles = new ArrayList<>();
    private final List<String> createdBodies = new ArrayList<>();
    private String stubbedUrl;
    private boolean shouldFail = false;

    public void setStubbedUrl(String url) {
        this.stubbedUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    @Override
    public String createDefect(String title, String body) {
        if (shouldFail) {
            throw new RuntimeException("GitHub Service Unavailable");
        }
        createdTitles.add(title);
        createdBodies.add(body);
        // Return a stable URL for testing if stubbed, otherwise a default
        return stubbedUrl != null ? stubbedUrl : "https://github.com/example/repo/issues/1";
    }

    public boolean wasCalledWithTitle(String title) {
        return createdTitles.contains(title);
    }
    
    public boolean wasCalled() {
        return !createdTitles.isEmpty();
    }
    
    public void reset() {
        createdTitles.clear();
        createdBodies.clear();
        this.stubbedUrl = null;
    }
}