package com.example.domain.validation.model;

/**
 * Value Object representing a valid GitHub Issue URL.
 * Enforces format validation to ensure defects are reported with actionable links.
 */
public class GitHubIssueUrl {
    private final String url;

    public GitHubIssueUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        // Validating GitHub URL format (https or http)
        // Regex explanation:
        // ^https?://                - Start with http:// or https://
        // github\.com/             - Domain
        // [^/]+/                   - Org/User (non-slash chars)
        // issues/                  - Literal 'issues/'
        // \d+                      - Issue ID (digits)
        // (/)?                     - Optional trailing slash
        // $
        // Note: In Java regex, backslashes must be escaped. \\\d becomes \\d in code, matching \d in regex.
        String regex = "^https?://github\\.com/[^/]+/issues/\\d+/?$";

        if (!url.matches(regex)) {
            throw new IllegalArgumentException("Invalid GitHub Issue URL format: " + url);
        }
        this.url = url;
    }

    public String value() {
        return url;
    }

    @Override
    public String toString() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GitHubIssueUrl that = (GitHubIssueUrl) o;
        return url.equals(that.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
