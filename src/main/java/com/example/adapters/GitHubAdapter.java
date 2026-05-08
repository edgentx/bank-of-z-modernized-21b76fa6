package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the GitHubPort.
 * Constructs URLs based on the configured repository base.
 */
@Component
@ConditionalOnProperty(name = "app.adapters.github.enabled", havingValue = "true", matchIfMissing = true)
public class GitHubAdapter implements GitHubPort {

    private final String baseUrl;

    public GitHubAdapter(@Value("${app.adapters.github.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String constructIssueUrl(String issueId) {
        return baseUrl + issueId;
    }
}
