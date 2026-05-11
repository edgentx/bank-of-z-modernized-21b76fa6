package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubPort for testing.
 */
public class MockGitHubAdapter implements GitHubPort {

    private final Map<String, String> responses = new HashMap<>();
    private String lastTitle;
    private String lastBody;

    public void mockResponse(String title, String url) {
        responses.put(title, url);
    }

    public String getLastTitle() {
        return lastTitle;
    }

    public String getLastBody() {
        return lastBody;
    }

    @Override
    public String createIssue(String title, String body) {
        this.lastTitle = title;
        this.lastBody = body;
        // Return a mock URL based on title if configured, or a default dummy URL
        return responses.getOrDefault(title, "https://github.com/egdcrypto/mock-repo/issues/1");
    }
}