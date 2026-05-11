package com.example.domain.defect.model;

/**
 * Value Object representing the GitHub URL.
 * Encapsulates the logic for generating the link.
 */
public record GitHubIssueUrl(String url) {
    public GitHubIssueUrl {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL cannot be blank");
        }
    }

    /**
     * Factory method to generate the URL based on the current defect ID convention.
     * Simulating the link creation logic expected by the defect report.
     */
    public static GitHubIssueUrl forDefect(String defectId) {
        // In a real system, this might call a GitHubPort to create the issue first.
        // For this validation, we assume the URL is predictably formatted.
        return new GitHubIssueUrl("https://github.com/example-bank/repos/issues/" + defectId.replace("-", ""));
    }
}
