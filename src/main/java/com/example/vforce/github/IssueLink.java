package com.example.vforce.github;

/**
 * Value object representing a GitHub Issue URL.
 * This replaces the missing 'String url' with a strong type.
 */
public record IssueLink(String url) {
    public IssueLink {
        if (url == null || url.isBlank()) throw new IllegalArgumentException("URL cannot be blank");
    }
}