package com.example.ports;

/**
 * Port for interacting with GitHub Issues.
 */
public interface GitHubPort {
    /**
     * Creates a GitHub issue for the given defect title and description.
     * @param title The defect title.
     * @param description The defect description/body.
     * @return The full HTML URL to the created issue.
     */
    String createIssue(String title, String description);
}
