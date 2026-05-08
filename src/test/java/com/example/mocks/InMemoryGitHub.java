package com.example.mocks;

import com.example.ports.GitHubPort;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of GitHubPort for testing.
 * Allows stubbing of issue creation URLs.
 */
public class InMemoryGitHub implements GitHubPort {

    private final Map<String, String> stubbedIssues = new HashMap<>();
    private String defaultUrl = "https://github.com/fake-org/project/issues/1";

    /**
     * Stubs the URL to be returned when creating an issue with a specific title.
     */
    public void stubCreateIssue(String title, String url) {
        stubbedIssues.put(title, url);
    }

    @Override
    public String createIssue(String title, String body) {
        if (stubbedIssues.containsKey(title)) {
            return stubbedIssues.get(title);
        }
        return defaultUrl;
    }
}
