package com.example.ports;

/**
 * Port for creating issues in GitHub.
 */
public interface GitHubIssuePort {
    
    /**
     * Creates a new issue in GitHub.
     *
     * @param defectId    The ID of the defect
     * @param title       The title of the issue
     * @param body        The body content of the issue
     * @return            The URL of the created issue
     */
    String createIssue(String defectId, String title, String body);
}
