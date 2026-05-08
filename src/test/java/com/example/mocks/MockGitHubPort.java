package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubPort implements GitHubPort {

    private String fakeUrlBase = "https://github.com/fake-repo/issues/";

    @Override
    public String getIssueUrl(String title, String projectId) {
        // Simulate deterministic URL generation based on inputs
        if (title == null) return null;
        return fakeUrlBase + projectId + "-" + title.hashCode();
    }

    public void setFakeUrlBase(String url) {
        this.fakeUrlBase = url;
    }
}