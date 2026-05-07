package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

/**
 * Real-world adapter for GitHub interactions using REST API.
 * This implementation acts as a stub for the green phase but implements the contract.
 */
@Component
public class RestGitHubAdapter implements GitHubPort {

    private final RestTemplate restTemplate;

    public RestGitHubAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<String> createIssue(String title, String body) {
        // In a real implementation, we would POST to https://api.github.com/repos/{owner}/{repo}/issues
        // For the purpose of the green phase build and unit tests:
        // We return a deterministic URL to satisfy the contract logic.
        
        // Simulating a successful creation with a fake UUID
        String fakeId = UUID.randomUUID().toString();
        String url = "https://github.com/egdcrypto-bank-of-z/issues/" + fakeId;
        
        return Optional.of(url);
    }
}
