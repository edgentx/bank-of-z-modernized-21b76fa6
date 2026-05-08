package com.example.ports;

/**
 * Port interface for creating GitHub Issues.
 * Abstracts the external GitHub API calls.
 */
public interface GitHubIssuePort {

    /**
     * Creates an issue in the repository.
     * @param title The issue title
     * @param body The issue body
     * @param labels Comma separated labels
     * @return Response containing the URL of the created issue.
     */
    GitHubIssueResponse createIssue(String title, String body, String labels);

    /**
     * Simple DTO for the response.
     */
    class GitHubIssueResponse {
        private final String url;
        public GitHubIssueResponse(String url) { this.url = url; }
        public String getUrl() { return url; }
    }
}
