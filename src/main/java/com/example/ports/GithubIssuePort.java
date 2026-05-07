package com.example.ports;

/**
 * Port interface for creating GitHub issues.
 * Used by the ReportDefectWorkflow to log defects.
 */
public interface GithubIssuePort {
    /**
     * Creates a new issue in the repository.
     * @param title The issue title (e.g. VW-454)
     * @param body The issue body/description
     * @return The URL of the created issue, or null if creation failed.
     */
    String createIssue(String title, String body);
}
