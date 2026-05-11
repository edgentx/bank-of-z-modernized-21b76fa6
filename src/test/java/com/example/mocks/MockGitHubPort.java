package com.example.mocks;

import com.example.ports.GitHubPort;

public class MockGitHubPort implements GitHubPort {
    private String mockUrl = "https://github.com/egdcrypto/bank-of-z/issues/1";
    
    @Override
    public String createIssue(String title, String body) {
        // Simulate GitHub returning a URL
        return mockUrl;
    }

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }
}
