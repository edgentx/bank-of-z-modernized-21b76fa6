package com.example.mocks;

import com.example.ports.GitHubPort;

public class MockGitHubPort implements GitHubPort {

    private String baseUrlFormat = "https://github.com/example/bank/issues/%s";

    @Override
    public String generateIssueUrl(String issueId) {
        if (issueId == null || issueId.isBlank()) {
            throw new IllegalArgumentException("Issue ID cannot be null");
        }
        // Simulating simple ID logic for testing: extract number if present, or use ID directly
        String normalizedId = issueId.replace("VW-", "");
        if (!normalizedId.matches("\\d+")) {
             // If it's not just digits, we just append it for the mock to be generic
             return "https://github.com/example/bank/issues/" + issueId;
        }
        return String.format(baseUrlFormat, normalizedId);
    }

    @Override
    public String createIssue(String title, String description) {
        // Returns a fake URL for the created issue
        return "https://github.com/example/bank/issues/999";
    }

    public void setBaseUrlFormat(String format) {
        this.baseUrlFormat = format;
    }
}