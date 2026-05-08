package com.example.ports;

/**
 * Port for creating GitHub issues.
 * This is the interface that the production code will implement
 * to talk to the real GitHub API, and which tests will mock.
 */
public interface GitHubPort {
    /**
     * Creates a GitHub issue for the given defect.
     * @return The full HTML URL of the created issue.
     */
    String createIssue(String title, String body);
}
