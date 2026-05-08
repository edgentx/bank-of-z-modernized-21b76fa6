package com.example.infrastructure.adapters.github;

import java.util.List;

/**
 * DTO for creating a GitHub Issue via API.
 */
public record GitHubIssueRequest(
    String title,
    String body,
    List<String> labels
) {}
