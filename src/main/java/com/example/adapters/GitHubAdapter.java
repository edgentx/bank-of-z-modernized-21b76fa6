package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Real Adapter for GitHub interactions.
 * Constructs URLs for GitHub Issues based on configuration.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private final String baseUrl;

    public GitHubAdapter(@Value("${github.base-url:https://github.com/fake-repo/issues}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String createIssueUrl(String issueKey) {
        if (baseUrl.endsWith("/")) {
            return baseUrl + issueKey;
        }
        return baseUrl + "/" + issueKey;
    }
}
