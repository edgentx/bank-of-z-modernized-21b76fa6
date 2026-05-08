package com.example.ports.dto;

/**
 * Response DTO for GitHub issue creation.
 * Must contain the URL of the created issue.
 */
public record IssueResponse(
    String url,
    String issueId
) {}
