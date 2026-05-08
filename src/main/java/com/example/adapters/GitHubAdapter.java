package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Real-world adapter implementation for GitHub issues.
 * Connects to the GitHub REST API to fetch issue details and construct URLs.
 */
@Component
public class GitHubAdapter implements GitHubIssuePort {

    private static final Logger log = LoggerFactory.getLogger(GitHubAdapter.class);
    
    // Hardcoded for the context of this story (VForce360)
    private static final String GITHUB_REPO = "bank-of-z/vforce360";
    private static final String GITHUB_API_BASE = "https://api.github.com/repos/";
    private static final String GITHUB_WEB_BASE = "https://github.com/";

    private final RestTemplate restTemplate;

    public GitHubAdapter() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Attempts to retrieve the URL of a GitHub issue.
     * Logic strips 'VW-' prefix to find the numeric ID.
     */
    @Override
    public Optional<String> getIssueUrl(String issueId) {
        if (issueId == null || issueId.isBlank()) {
            return Optional.empty();
        }

        try {
            // Heuristic: Extract numeric ID. VW-454 -> 454.
            String numericId = issueId.replaceAll("[^0-9]", "");
            if (numericId.isEmpty()) {
                log.warn("Could not extract numeric ID from issue ID: {}", issueId);
                return Optional.empty();
            }

            // Check if issue exists (HEAD or GET request)
            String apiUrl = GITHUB_API_BASE + GITHUB_REPO + "/issues/" + numericId;
            
            // Execute request. If 404, return empty. If 200, construct web URL.
            // We assume existence implies success for this defect scenario.
            restTemplate.getForObject(apiUrl, String.class);

            String webUrl = GITHUB_WEB_BASE + GITHUB_REPO + "/issues/" + numericId;
            return Optional.of(webUrl);

        } catch (Exception e) {
            // Log failure but don't crash the application (Defect report should still go to Slack)
            log.warn("GitHub lookup failed for {}: {}", issueId, e.getMessage());
            return Optional.empty();
        }
    }
}
