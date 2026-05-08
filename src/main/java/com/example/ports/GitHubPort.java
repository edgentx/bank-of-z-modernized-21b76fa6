package com.example.ports;

/**
 * Port interface for interacting with GitHub issues.
 */
public interface GitHubPort {
    
    /**
     * Creates a GitHub issue URL or retrieves an existing one.
     * 
     * @param title Title of the issue.
     * @param projectId Project identifier.
     * @return The fully qualified URL to the GitHub issue.
     */
    String getIssueUrl(String title, String projectId);
}