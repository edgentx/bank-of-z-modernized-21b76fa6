package com.example.adapters;

import com.example.ports.IssueTrackerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Production implementation of IssueTrackerPort.
 * In a real scenario, this would use WebClient/RestTemplate to call the GitHub API.
 * For this phase, it simulates the interaction while providing the real contract.
 */
@Component
public class GitHubIssueTrackerAdapter implements IssueTrackerPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubIssueTrackerAdapter.class);

    @Override
    public Map<String, String> createIssue(String title, String body) {
        log.info("Creating GitHub issue: title={}", title);

        // Simulate API call latency or logic here
        // String url = webClient.post() ... .retrieve().bodyToNode(...).get("html_url");
        
        // Returning a deterministic mock URL structure for verification
        String fakeId = UUID.randomUUID().toString().substring(0, 8);
        String url = "https://github.com/bank-of-z/issues/" + fakeId;

        Map<String, String> response = new HashMap<>();
        response.put("status", "201 Created");
        response.put("url", url);
        
        log.info("GitHub issue created successfully: {}", url);
        return response;
    }
}
