package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used to generate the URL required in the Slack body.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new issue on GitHub.
     *
     * @param title The issue title.
     * @param body  The issue body.
     * @return The URL of the created issue.
     * @throws IssueCreationException if creation fails.
     */
    String createIssue(String title, String body);

    class IssueCreationException extends RuntimeException {
        public IssueCreationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
