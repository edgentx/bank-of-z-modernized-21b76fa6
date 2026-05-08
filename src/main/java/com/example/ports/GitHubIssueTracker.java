package com.example.ports;

/**
 * Port for interacting with GitHub Issues.
 * Used by the validation workflow to link defects to code.
 */
public interface GitHubIssueTracker {
    /**
     * Creates a new issue in the repository.
     *
     * @param title The title of the issue
     * @param body The description/body of the issue
     * @param labels Labels to categorize the issue (e.g., "bug", "severity:low")
     * @return The HTML URL of the created issue
     */
    String createIssue(String title, String body, String... labels);
}
