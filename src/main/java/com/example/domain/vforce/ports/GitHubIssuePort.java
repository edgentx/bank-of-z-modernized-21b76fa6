package com.example.domain.vforce.ports;

/**
 * Port for creating GitHub issues.
 */
public interface GitHubIssuePort {

    /**
     * Creates an issue in the repository.
     * @param title The issue title.
     * @param body The issue body.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String body);
}