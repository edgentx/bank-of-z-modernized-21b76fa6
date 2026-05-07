package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of GitHubPort for testing.
 * Allows verification of inputs and control of return values.
 */
public class MockGitHubAdapter implements GitHubPort {

    private final List<String> receivedTitles = new ArrayList<>();
    private final List<String> receivedBodies = new ArrayList<>();
    private String mockUrl = "https://github.com/mock-repo/issues/1";

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    @Override
    public String createIssue(String title, String body) {
        receivedTitles.add(title);
        receivedBodies.add(body);
        // Simulate a new URL for each call to ensure uniqueness
        return mockUrl + "?id=" + receivedTitles.size();
    }

    public String getLastTitle() {
        if (receivedTitles.isEmpty()) return null;
        return receivedTitles.get(receivedTitles.size() - 1);
    }

    public String getLastBody() {
        if (receivedBodies.isEmpty()) return null;
        return receivedBodies.get(receivedBodies.size() - 1);
    }

    public void reset() {
        receivedTitles.clear();
        receivedBodies.clear();
    }
}
