package com.example.mocks;

import com.example.domain.vforce360.model.DefectAggregate;
import com.example.ports.GitHubClient;

/**
 * Mock implementation of GitHubClient for testing.
 */
public class MockGitHubClient implements GitHubClient {

    private String mockUrl = "https://github.com/mocked-org/repo/issues/1";

    @Override
    public String createIssue(DefectAggregate defect) {
        return mockUrl;
    }

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }
}
