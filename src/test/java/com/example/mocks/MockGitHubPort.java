package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubPort.
 * Returns predictable URLs for testing without hitting api.github.com.
 */
public class MockGitHubPort implements GitHubPort {

    private final Map<String, String> mockUrls = new HashMap<>();
    private boolean createIssueCalled = false;
    private String lastGeneratedUrl;

    public void setMockCreatedIssueUrl(String defectId, String url) {
        mockUrls.put(defectId, url);
    }

    @Override
    public String createIssue(String defectId, String title, String body) {
        createIssueCalled = true;
        // Return a predictable default or the mocked one
        lastGeneratedUrl = mockUrls.getOrDefault(defectId, "https://github.com/bank-of-z/vforce360/issues/0");
        return lastGeneratedUrl;
    }

    public boolean wasCreateIssueCalled() {
        return createIssueCalled;
    }

    public String getLastGeneratedUrl() {
        return lastGeneratedUrl;
    }

    public void reset() {
        createIssueCalled = false;
        lastGeneratedUrl = null;
        mockUrls.clear();
    }
}
