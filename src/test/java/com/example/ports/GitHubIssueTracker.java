package com.example.ports;

import com.example.domain.validation.model.ReportDefectCommand;

/**
 * Port interface for interacting with GitHub issues.
 * Used to create a defect ticket and retrieve its URL.
 */
public interface GitHubIssueTracker {
    /**
     * Creates an issue in GitHub based on the defect command.
     * @param cmd the defect details
     * @return the fully qualified URL to the created GitHub issue
     */
    String createIssue(ReportDefectCommand cmd);
}
