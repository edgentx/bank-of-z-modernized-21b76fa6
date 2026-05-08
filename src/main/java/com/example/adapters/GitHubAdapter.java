package com.example.adapters;

import com.example.ports.IssueTrackingPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Real implementation of the IssueTrackingPort.
 * In a production environment, this would connect to GitHub API or JIRA API.
 * For this implementation phase, it simulates the creation or performs a REST call.
 */
@Component
public class GitHubAdapter implements IssueTrackingPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubAdapter.class);
    private static final String MOCK_BASE_URL = "http://github.example.com/real-repo/issues/";

    /**
     * Creates an issue.
     * Note: Currently acts as a placeholder for the actual HTTP client implementation.
     * The critical part is returning a valid String URL.
     */
    @Override
    public String createIssue(String title, String description) {
        // Simulation of creating an issue
        String issueId = UUID.randomUUID().toString();
        String url = MOCK_BASE_URL + issueId;
        
        log.info("[GitHubAdapter] Created issue '{}' with URL: {}", title, url);
        
        // In a real implementation, we would use WebClient/RestTemplate here:
        // return webClient.post().uri("/repos/{owner}/{repo}/issues")...
        
        return url;
    }
}
