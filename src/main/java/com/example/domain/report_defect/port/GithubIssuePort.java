package com.example.domain.report_defect.port;

/**
 * Port interface for creating GitHub issues.
 * Abstracts the HTTP client implementation used by the domain/infrastructure.
 */
public interface GithubIssuePort {
    
    /**
     * Creates an issue in the repository.
     * @param title The issue title (usually Defect ID)
     * @param body The defect description
     * @param label The label to apply (e.g. 'bug')
     * @return A response object containing the generated URL
     */
    GithubIssueResponse createIssue(String title, String body, String label);
}
