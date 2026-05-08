package com.example.mocks;

import com.example.ports.GitHubIssuePort;
import java.util.Optional;

/**
 * Mock implementation of GitHubIssuePort for testing.
 * Allows configuring return values for issue creation.
 */
public class MockGitHubIssuePort implements GitHubIssuePort {

    private Optional<String> nextResult = Optional.empty();
    private String lastTitle;
    private String lastBody;

    public void mockCreateIssueResult(String url) {
        this.nextResult = Optional.ofNullable(url);
    }

    @Override
    public Optional<String> createIssue(String title, String body) {
        this.lastTitle = title;
        this.lastBody = body;
        return nextResult;
    }

    public String getLastTitle() { return lastTitle; }
    public String getLastBody() { return lastBody; }
}
