package com.example.mocks;

import com.example.ports.GitHubPort;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubPort implements GitHubPort {
    
    private String returnUrl = "https://github.com/mock/issues/1";
    
    @Override
    public String createIssue(String title, String body, String type) {
        System.out.println("[MockGitHub] Issue Created: " + title);
        return returnUrl;
    }

    public void setReturnUrl(String url) {
        this.returnUrl = url;
    }
}