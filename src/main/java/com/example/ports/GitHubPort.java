package com.example.ports;

/**
 * Port for interacting with GitHub issues.
 * Used to generate URLs and track defect reports.
 */
public interface GitHubPort {

    /**
     * Generates the full HTML link for a GitHub issue.
     *
     * @param issueId The unique identifier of the issue (e.g., "VW-454")
     * @return The full URL string (e.g., "https://github.com/org/repo/issues/454")
     * @throws IllegalArgumentException if issueId is null or empty
     */
    String generateIssueUrl(String issueId);

    /**
     * Creates a new issue on GitHub and returns its URL.
     * For this defect validation, we might expect this to be called, or we might verify the URL format.
     */
    String createIssue(String title, String description);
}