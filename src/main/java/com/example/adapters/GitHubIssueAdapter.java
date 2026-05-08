package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of {@link GitHubIssuePort}.
 * Constructs URLs for the Bank-of-Z issue tracker.
 */
@Component
public class GitHubIssueAdapter implements GitHubIssuePort {

    private final String baseUrl;

    public GitHubIssueAdapter(@Value("${github.base-url:https://github.com/bank-of-z/issues/issues/}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String getIssueUrl(String issueId) {
        if (issueId == null || issueId.isBlank()) {
            throw new IllegalArgumentException("issueId must not be blank");
        }
        return baseUrl + issueId;
    }
}
