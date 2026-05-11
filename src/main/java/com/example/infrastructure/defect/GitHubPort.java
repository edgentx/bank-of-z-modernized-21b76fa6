package com.example.infrastructure.defect;

/**
 * Port interface for creating GitHub Issues.
 * Abstracted to allow mocking in tests.
 */
public interface GitHubPort {
    
    /**
     * Creates a new issue on GitHub.
     * @param title The issue title
     * @param body The issue body/description
     * @return The full HTML URL of the created issue
     */
    String createIssue(String title, String body);
}
