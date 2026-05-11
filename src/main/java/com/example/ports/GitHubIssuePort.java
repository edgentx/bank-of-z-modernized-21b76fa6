package com.example.ports;

import com.example.domain.shared.ReportDefectCmd;

/**
 * Port for creating GitHub issues.
 */
public interface GitHubIssuePort {

    /**
     * Creates a remote GitHub issue based on the defect report.
     *
     * @param cmd The defect command.
     * @return The HTML URL of the created issue.
     */
    String createIssue(ReportDefectCmd cmd);
}
