package com.vforce360.ports.github;

/**
 * Port interface for interacting with GitHub Issues.
 * This isolates the core logic from the specific GitHub client implementation.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new issue in the specified repository.
     *
     * @param repoOwner The repository owner (e.g., "my-org").
     * @param repoName The repository name (e.g., "vforce360-core").
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @return The HTML URL of the created issue (e.g., "https://github.com/.../issues/454").
     */
    String createIssue(String repoOwner, String repoName, String title, String body);
}
