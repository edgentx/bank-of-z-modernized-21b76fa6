package com.example.ports;

/**
 * Port interface for creating issues in GitHub.
 */
public interface GitHubPort {
    /**
     * Creates a GitHub issue for the given defect summary and description.
     * @param summary The defect summary/title.
     * @param description The defect description/body.
     * @return The full URL of the created issue.
     */
    String createIssue(String summary, String description);
}