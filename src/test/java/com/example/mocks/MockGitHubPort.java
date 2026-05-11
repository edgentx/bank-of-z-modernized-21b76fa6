package com.example.mocks;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;

@Component
public class MockGitHubPort implements GitHubPort {
    private String mockUrl = "https://github.com/egdcrypto/default-issue";

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    @Override
    public String createIssue(String description) {
        // Simulate API call delay/logic if needed, returning mock URL
        return mockUrl;
    }
}