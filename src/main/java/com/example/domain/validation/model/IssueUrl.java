package com.example.domain.validation.model;

/**
 * Value object representing a valid Issue URL.
 */
public record IssueUrl(String url) {
    public IssueUrl {
        if (url == null || url.isBlank()) throw new IllegalArgumentException("URL cannot be blank");
    }
}
