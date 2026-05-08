package com.example.ports;

import com.example.domain.shared.ReportDefectCommand;

/**
 * Port interface for interacting with GitHub Issues API.
 * Used by the workflow adapter to create tickets.
 */
public interface GitHubIssuePort {
    /**
     * Creates an issue in GitHub.
     * @param cmd Defect details.
     * @return The URL of the created issue.
     */
    String createIssue(ReportDefectCommand cmd);
}
