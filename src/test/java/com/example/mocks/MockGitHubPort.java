package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.ArrayList;
import java.util.List;

public class MockGitHubPort implements GitHubPort {
    private final List<String> createdIssues = new ArrayList<>();
    private String mockUrl = "https://github.com/test/repo/issues/1";

    @Override
    public String createIssue(String title, String body) {
        System.out.println("[MOCK] Creating GitHub Issue: " + title);
        createdIssues.add(title);
        return mockUrl;
    }

    public void setMockUrl(String url) {
        this.mockUrl = url;
    }

    public List<String> getCreatedIssues() {
        return createdIssues;
    }
}
