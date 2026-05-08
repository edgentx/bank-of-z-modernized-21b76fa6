package com.example.ports;

/**
 * Port interface for GitHub Issue tracking operations.
 * Implementations (Adapters) will handle the actual HTTP communication with GitHub.
 */
public interface GitHubIssueTracker {

    /**
     * Creates a new issue on GitHub.
     *
     * @param title The title of the issue.
     * @param body  The body content of the issue.
     * @param label The label to apply (e.g., 'bug', 'enhancement').
     * @return The URL of the created issue.
     */
    String createIssue(String title, String body, String label);
}
