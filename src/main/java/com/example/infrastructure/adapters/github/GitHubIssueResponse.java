package com.example.infrastructure.adapters.github;

/**
 * DTO representing the response from GitHub Issue creation.
 */
public record GitHubIssueResponse(
    String htmlUrl,
    String id,
    String nodeId,
    String state
) {}
