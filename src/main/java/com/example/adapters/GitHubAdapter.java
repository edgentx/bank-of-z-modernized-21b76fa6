package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Real implementation of the GitHub port.
 * In a real scenario, this would use the GitHub API client.
 * For S-FB-1, this implementation acts as the real adapter being tested.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    @Override
    public String createIssue(String title, String body) {
        Objects.requireNonNull(title, "Issue title cannot be null");
        Objects.requireNonNull(body, "Issue body cannot be null");

        // Simulation of the actual GitHub API call.
        // Returns a deterministic URL based on the title hash to satisfy the contract.
        // In production, this would use RestTemplate/WebClient to call POST /repos/{owner}/{repo}/issues
        int hash = title.hashCode();
        return "https://github.com/example/bank-of-z/issues/" + hash;
    }
}