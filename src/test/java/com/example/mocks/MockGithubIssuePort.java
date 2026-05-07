package com.example.mocks;

import com.example.ports.GithubIssuePort;

/**
 * Mock implementation of GithubIssuePort for testing.
 * Returns deterministic URLs for issue creation.
 */
public class MockGithubIssuePort implements GithubIssuePort {

    private int issueCount = 0;
    private boolean shouldFail = false;

    @Override
    public String createIssue(String title, String body) {
        if (shouldFail) {
            throw new RuntimeException("GitHub API unavailable (simulated)");
        }
        issueCount++;
        // Return a deterministic URL matching the defect expectations
        return "https://github.com/example/bank-z/issues/" + issueCount;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    public int getIssueCount() {
        return issueCount;
    }
}
