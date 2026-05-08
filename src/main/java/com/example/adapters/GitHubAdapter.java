package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Real-world implementation of GitHubPort.
 * Generates the URL based on the configured repository.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    @Value("${github.repo.base-url:https://github.com/example/issues/}")
    private String baseUrl;

    @Override
    public String getIssueUrl(String issueId) {
        return baseUrl + issueId;
    }
}