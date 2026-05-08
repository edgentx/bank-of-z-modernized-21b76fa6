package com.example.domain.validation.model;

import com.example.domain.shared.ValueObject;
import java.util.Objects;

/**
 * Value Object representing a valid GitHub Issue URL.
 * Enforces basic format validation.
 */
public class GitHubIssueUrl implements ValueObject {
    private final String url;

    public GitHubIssueUrl(String url) {
        if (url == null || !url.matches("^https://github\.com/.*/issues/\d+$")) {
            throw new IllegalArgumentException("Invalid GitHub Issue URL format: " + url);
        }
        this.url = url;
    }

    public String value() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GitHubIssueUrl that = (GitHubIssueUrl) o;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    @Override
    public String toString() {
        return url;
    }
}
