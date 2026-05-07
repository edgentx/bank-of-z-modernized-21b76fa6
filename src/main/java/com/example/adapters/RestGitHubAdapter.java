package com.example.adapters;

import com.example.ports.GitHubPort;
import com.example.vforce.github.IssueLink;
import com.example.vforce.shared.ReportDefectCommand;
import org.springframework.web.client.RestTemplate;

/**
 * Real implementation of GitHubPort using RestTemplate.
 * This is the adapter that would make real HTTP calls in a live environment.
 */
public class RestGitHubAdapter implements GitHubPort {

    private final RestTemplate restTemplate;
    private final String repoUrl;
    private final String authToken;

    public RestGitHubAdapter(RestTemplate restTemplate, String repoUrl, String authToken) {
        this.restTemplate = restTemplate;
        this.repoUrl = repoUrl;
        this.authToken = authToken;
    }

    @Override
    public IssueLink createIssue(ReportDefectCommand command) {
        // In a real scenario, we would construct a GitHub Issue JSON payload
        // and POST it to repoUrl/issues.
        // For now, we return a dummy link to satisfy the compiler and pattern.
        
        // String result = restTemplate.postForObject(repoUrl + "/issues", payload, String.class);
        
        return new IssueLink(repoUrl + "/issues/1");
    }
}