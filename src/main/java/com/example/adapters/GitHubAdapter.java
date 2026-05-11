package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;

/**
 * GitHub implementation for resolving issue URLs.
 * Constructs standard GitHub links based on the configured repository base.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private final String baseUrl;

    public GitHubAdapter() {
        // Default constructor constructing the standard base URL for the project
        this.baseUrl = "https://github.com/egdcrypto/bank-of-z/issues";
    }

    @Override
    public String getIssueUrl(String issueId) {
        if (issueId == null || issueId.isBlank()) {
            throw new IllegalArgumentException("issueId cannot be null or blank");
        }
        return baseUrl + "/" + issueId;
    }
}
