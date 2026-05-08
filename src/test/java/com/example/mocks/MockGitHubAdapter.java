package com.example.mocks;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;

/**
 * Mock implementation for GitHub API interactions.
 * Returns predictable URLs for testing.
 */
@Component
public class MockGitHubAdapter implements GitHubPort {

    private String mockUrl = "https://github.com/mock/issues/1";
    private boolean shouldThrowException = false;

    @Override
    public String createIssue(String defectCode, String summary, String severity) {
        if (shouldThrowException) {
            throw new RuntimeException("GitHub API unavailable");
        }
        // Return a deterministic URL based on input for verification
        return "https://github.com/fake-repo/issues/" + defectCode.replace("-", "");
    }

    public void setMockIssueUrl(String url) {
        this.mockUrl = url;
    }

    public void setThrowException(boolean flag) {
        this.shouldThrowException = flag;
    }

    public void reset() {
        this.mockUrl = "https://github.com/mock/issues/1";
        this.shouldThrowException = false;
    }
}
