package com.example.mocks;

import com.example.ports.GitHubPort;

public class MockGitHubPort implements GitHubPort {

    private String lastTitle;
    private String lastBody;
    private String fakeUrl = "https://github.com/fake-repo/issues/1";

    @Override
    public String createIssue(String title, String body) {
        this.lastTitle = title;
        this.lastBody = body;
        return this.fakeUrl;
    }

    public String getLastTitle() {
        return lastTitle;
    }

    public String getLastBody() {
        return lastBody;
    }

    /**
     * Sets the URL to be returned by the mock (simulating GitHub response).
     * Useful for testing URL formatting specifically.
     */
    public void setFakeUrl(String fakeUrl) {
        this.fakeUrl = fakeUrl;
    }
}
