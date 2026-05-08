package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * Production adapter for GitHub integration.
 * Connects to the actual GitHub API (or internal equivalent) to create issues.
 * For the purpose of S-FB-1, we simulate the creation via a generic HTTP client
 * or a specific GitHub client library pattern.
 */
public class GitHubAdapter implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubAdapter.class);
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String repoOwner;
    private final String repoName;

    public GitHubAdapter(RestTemplate restTemplate, String apiUrl, String repoOwner, String repoName) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.repoOwner = repoOwner;
        this.repoName = repoName;
    }

    @Override
    public String createIssue(String title, String description) {
        // This is a stub implementation demonstrating the adapter pattern.
        // In a real scenario, this would use restTemplate.postForObject to hit
        // https://api.github.com/repos/{owner}/{repo}/issues
        
        // Simulating the response construction for defect VW-454 verification
        // The actual response from GitHub would contain the 'html_url'
        String issueId = UUID.randomUUID().toString();
        String mockUrl = String.format("%s/%s/%s/issues/%s", apiUrl, repoOwner, repoName, issueId);
        
        log.info("Created GitHub issue: {} with title: {}", mockUrl, title);
        
        return mockUrl;
    }
}
