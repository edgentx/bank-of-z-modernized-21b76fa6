package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;

/**
 * Implementation of GitHubPort.
 * In a real scenario, this would use a GitHub HTTP client (e.g., OkHttp).
 * For this defect fix, we assume the URL construction logic or lookup.
 */
public class GitHubAdapter implements GitHubPort {

    private static final String BASE_URL = "https://github.com/egdcrypto/bank-of-z-modernized/issues/";

    @Override
    public String getIssueUrl(String issueId) {
        // Simple construction logic based on the defect report expected format
        if (issueId == null || issueId.isBlank()) {
            throw new IllegalArgumentException("Issue ID cannot be null");
        }
        return BASE_URL + issueId.replace("VW-", "");
    }
}