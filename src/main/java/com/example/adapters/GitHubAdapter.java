package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Real implementation of GitHubPort.
 * Constructs URLs based on a configured base repository URL.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private final String baseUrl;

    public GitHubAdapter(@Value("${vforce.github.issue-url-base:https://github.com/bank-of-z/vforce360/issues/}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String getIssueUrl(String defectId) {
        return baseUrl + defectId;
    }
}
