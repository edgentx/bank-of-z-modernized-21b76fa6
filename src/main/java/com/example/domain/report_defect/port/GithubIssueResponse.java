package com.example.domain.report_defect.port;

import java.time.Instant;
import java.util.Objects;

/**
 * Value object representing the response from GitHub issue creation.
 */
public class GithubIssueResponse {
    private final String url;
    private final String issueId;
    private final Instant createdAt;

    public GithubIssueResponse(String url, String issueId, Instant createdAt) {
        this.url = url;
        this.issueId = issueId;
        this.createdAt = createdAt;
    }

    public String getUrl() {
        return url;
    }

    public String getIssueId() {
        return issueId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GithubIssueResponse that = (GithubIssueResponse) o;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
