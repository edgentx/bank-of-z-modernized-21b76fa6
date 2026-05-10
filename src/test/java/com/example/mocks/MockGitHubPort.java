package com.example.mocks;

import com.example.ports.GitHubPort;

public class MockGitHubPort implements GitHubPort {
    private boolean shouldReturnValidUrl = true;
    private String fakeUrl = "https://github.com/example/issues/1";

    @Override
    public String createIssue(String title, String description) {
        if (shouldReturnValidUrl) {
            return fakeUrl;
        }
        return null;
    }

    public void setFakeUrl(String url) {
        this.fakeUrl = url;
    }
}
