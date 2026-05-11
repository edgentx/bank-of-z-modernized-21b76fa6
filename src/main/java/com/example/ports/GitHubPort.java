package com.example.ports;

import com.example.domain.defect.model.LinkGitHubIssueCmd;

/**
 * Port for interacting with GitHub issues.
 * Used to create issues and retrieve their URLs for defect tracking.
 */
public interface GitHubPort {

    /**
     * Creates a GitHub issue based on the defect details.
     *
     * @param cmd The command containing summary (title) and description (body).
     * @return The fully qualified URL of the created issue.
     * @throws RuntimeException if the API call fails or returns invalid data.
     */
    String createIssue(LinkGitHubIssueCmd cmd);
}
