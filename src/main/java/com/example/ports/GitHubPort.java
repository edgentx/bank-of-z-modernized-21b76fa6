package com.example.ports;

/**
 * Port for interacting with GitHub Issues API.
 * Implementations must handle the creation of issues and URL retrieval.
 */
public interface GitHubPort {
    /**
     * Creates a defect report in GitHub and returns the direct URL to the issue.
     * @param title The defect title
     * @param body The defect body/description
     * @return The full URL to the created GitHub issue (e.g., https://github.com/org/repo/issues/123)
     */
    String createDefect(String title, String body);
}