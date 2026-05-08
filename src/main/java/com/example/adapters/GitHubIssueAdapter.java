package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GitHubIssueAdapter implements GitHubPort {

    private final RestTemplate restTemplate;

    public GitHubIssueAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String createIssue(String repo, String title, String body) {
        // Stub implementation satisfying the contract.
        throw new UnsupportedOperationException("Real GitHub implementation pending credential config");
    }
}
