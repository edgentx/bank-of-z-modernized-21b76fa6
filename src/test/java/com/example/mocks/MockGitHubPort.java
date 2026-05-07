package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock adapter for GitHub interactions.
 * Returns deterministic URLs for testing.
 */
public class MockGitHubPort implements GitHubPort {
    @Override
    public String createIssue(String defectId, String title, String description) {
        // Return a predictable URL based on the defect ID
        return "https://github.com/bank-of-z/issues/" + defectId;
    }
}
