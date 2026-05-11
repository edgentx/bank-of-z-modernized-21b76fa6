package com.example.mocks;

import com.example.ports.GitHubIssuePort;

import java.net.URI;

/**
 * Fake implementation of GitHubIssuePort.
 * Returns deterministic data without hitting the real GitHub API.
 */
public class FakeGitHubIssueAdapter implements GitHubIssuePort {

    private String mockUrlBase = "https://github.com/example/bank-of-z/issues/";
    private int counter = 1;
    private String specificUrlToReturn;

    @Override
    public URI createIssue(String title, String body) throws Exception {
        if (specificUrlToReturn != null) {
            return URI.create(specificUrlToReturn);
        }
        // Default fake behavior: return an incrementing URL
        String url = mockUrlBase + (counter++);
        return URI.create(url);
    }

    /**
     * Allows the test to force a specific URL to be returned,
     * useful for validating exact string matching in Slack body.
     */
    public void setUrlToReturn(String url) {
        this.specificUrlToReturn = url;
    }
}
