package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Legacy/Alternative GitHub Adapter using RestTemplate.
 * Included to resolve compilation errors in the provided context.
 */
@Component
public class GitHubIssueAdapter implements GitHubPort {

    private final RestTemplate restTemplate;

    public GitHubIssueAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String createIssue(String repo, String title, String body) {
        // Implementation to be completed in Green phase
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
