package com.example.application;

/**
 * Port interface for creating GitHub Issues.
 * Used by ValidationReportedHandler.
 */
public interface GitHubService {
    /**
     * Creates an issue in the repository.
     * @return The HTML URL of the created issue.
     */
    String createIssue(String title, String body);
}