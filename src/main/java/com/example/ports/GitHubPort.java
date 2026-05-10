package com.example.ports;

/**
 * Port for interacting with GitHub issue tracking.
 */
public interface GitHubPort {

    /**
     * Creates a GitHub issue for the given defect details.
     *
     * @param title Title of the defect.
     * @param description Description of the defect.
     * @return The URL of the created GitHub issue.
     */
    String createIssue(String title, String description);
}
