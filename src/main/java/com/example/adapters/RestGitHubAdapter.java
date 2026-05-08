package com.example.adapters;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Adapter for interacting with GitHub API.
 * Uses Spring's RestTemplate for synchronous HTTP calls.
 */
@Component
public class RestGitHubAdapter {

    private final RestTemplate restTemplate;

    public RestGitHubAdapter() {
        this.restTemplate = new RestTemplate();
    }

    public RestGitHubAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Creates a new issue in the configured GitHub repository.
     * 
     * @param title The title of the issue
     * @param body  The body content of the issue
     * @return The URL of the created issue
     */
    public String createIssue(String title, String body) {
        // Implementation stub for the GitHub API interaction.
        // In a real scenario, this would POST to https://api.github.com/repos/{owner}/{repo}/issues
        // using restTemplate.postForObject(...).
        
        // Returning a deterministic URL format for defect tracking.
        return "https://github.com/example/repo/issues/1";
    }
}