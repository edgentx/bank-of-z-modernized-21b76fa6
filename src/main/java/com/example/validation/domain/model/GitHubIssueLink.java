package com.example.validation.domain.model;

public record GitHubIssueLink(String url) {
    public GitHubIssueLink {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL must not be blank");
        }
    }
}
