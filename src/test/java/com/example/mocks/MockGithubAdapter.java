package com.example.mocks;

import com.example.ports.GithubPort;

/**
 * Mock implementation of GithubPort.
 * Returns predictable URLs without calling GitHub API.
 */
public class MockGithubAdapter implements GithubPort {

    private String mockUrlPrefix = "https://github.com/mock-org/issues/";
    private int callCount = 0;

    @Override
    public String createIssue(String title, String body) {
        callCount++;
        // Simulate a deterministic URL generation based on call count
        return mockUrlPrefix + callCount;
    }

    public int getCallCount() {
        return callCount;
    }

    public void setMockUrlPrefix(String prefix) {
        this.mockUrlPrefix = prefix;
    }
}
