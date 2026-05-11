package com.example.adapters;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.stereotype.Component;

/**
 * Adapter for interacting with GitHub API.
 * Currently just a stub for type resolution.
 */
@Component
public class GitHubRestAdapter {
    private final RestClient restClient;

    public GitHubRestAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    public String createIssue(String repo, String title, String body) {
        // Implementation would go here
        return "https://github.com/example/issues/1";
    }
}