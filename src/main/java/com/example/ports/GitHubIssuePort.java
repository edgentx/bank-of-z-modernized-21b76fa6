package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 */
public interface GitHubIssuePort {

    /**
     * Creates a new issue in the configured repository.
     *
     * @param title The title of the issue
     * @param description The body content of the issue
     * @return The HTML URL of the created issue (e.g. https://github.com/org/repo/issues/1)
     */
    String createIssue(String title, String description);
}
