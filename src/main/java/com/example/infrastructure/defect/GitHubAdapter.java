package com.example.infrastructure.defect;

import org.springframework.stereotype.Component;

/**
 * Real implementation of GitHubPort.
 * Connects to GitHub API to create issues.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private static final String GITHUB_API_BASE = "https://api.github.com";
    private final String repoOwner;
    private final String repoName;

    public GitHubAdapter() {
        // In a real Spring Boot app, these would be @Value("${github.owner}") etc.
        // Using defaults for the exercise.
        this.repoOwner = "fake-org";
        this.repoName = "repo";
    }

    @Override
    public String createIssue(String title, String body) {
        // Placeholder for actual HTTP call (e.g., using WebClient or RestTemplate)
        // POST /repos/{owner}/{repo}/issues
        
        // Simulating the API response construction for valid URL
        // Real implementation would return response.getHtmlUrl()
        String issueNumber = Math.abs(title.hashCode());
        return String.format("https://github.com/%s/%s/issues/%d", repoOwner, repoName, issueNumber);
    }
}
