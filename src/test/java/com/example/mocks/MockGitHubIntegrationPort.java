package com.example.mocks;

import com.example.ports.GitHubIntegrationPort;
import java.util.Optional;

/**
 * Mock implementation of GitHubIntegrationPort for testing.
 * Simulates retrieving GitHub issue URLs.
 */
public class MockGitHubIntegrationPort implements GitHubIntegrationPort {

    private String mockUrlToReturn;
    private boolean shouldReturnEmpty = false;

    public void setMockUrl(String url) {
        this.mockUrlToReturn = url;
        this.shouldReturnEmpty = false;
    }

    public void setEmptyResponse(boolean isEmpty) {
        this.shouldReturnEmpty = isEmpty;
    }

    @Override
    public Optional<String> getIssueUrl(String defectId) {
        if (shouldReturnEmpty) {
            return Optional.empty();
        }
        return Optional.ofNullable(mockUrlToReturn);
    }
}
