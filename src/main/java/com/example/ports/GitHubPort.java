package com.example.ports;

/**
 * Port interface for interacting with GitHub issues.
 * Implementations will handle the GitHub API calls.
 */
public interface GitHubPort {

    /**
     * Creates a new issue in the repository.
     *
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @return The URL of the created issue.
     * @throws GitHubException if creation fails.
     */
    String createIssue(String title, String body) throws GitHubException;

    class GitHubException extends RuntimeException {
        public GitHubException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
