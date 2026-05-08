package com.example.adapters;

import com.example.ports.IssueTrackerPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Real adapter for Issue Tracker.
 * Constructs GitHub URLs based on the configured repository base URL.
 */
@Component
public class IssueTrackerAdapter implements IssueTrackerPort {

    private final String baseUrl;

    public IssueTrackerAdapter(@Value("${issue.tracker.base.url:https://github.com/example/bank-of-z}") String baseUrl) {
        // Ensure base URL doesn't end with a slash for consistent formatting
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    @Override
    public String getIssueUrl(String issueId) {
        if (issueId == null || issueId.isBlank()) {
            return this.baseUrl + "/issues/unknown";
        }
        return this.baseUrl + "/issues/" + issueId;
    }
}
