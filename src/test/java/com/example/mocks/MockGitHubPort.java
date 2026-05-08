package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.ArrayList;
import java.util.List;

public class MockGitHubPort implements GitHubPort {
    private final List<String> createdIssues = new ArrayList<>();
    private String nextUrl = "https://github.com/example-repo/issues/1";

    @Override
    public String createIssue(String title, String body) {
        createdIssues.add(title);
        return nextUrl;
    }

    public void setNextUrl(String url) {
        this.nextUrl = url;
    }

    public boolean wasIssueCreated(String title) {
        return createdIssues.contains(title);
    }

    public void reset() {
        createdIssues.clear();
        nextUrl = "https://github.com/example-repo/issues/1";
    }
}
