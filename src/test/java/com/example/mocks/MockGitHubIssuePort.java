package com.example.mocks;

import com.example.ports.GitHubIssuePort;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Returns deterministic URLs based on input IDs.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    @Override
    public String getIssueUrl(String defectId) {
        // Return a deterministic fake URL based on the defect ID
        // Ensures the test environment doesn't rely on external GitHub state
        return "https://github.com/fake-org/bank-of-z/issues/" + defectId;
    }
}
