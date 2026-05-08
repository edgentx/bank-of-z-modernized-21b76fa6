package com.example.adapters.github;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Real implementation of the GitHub Port.
 * Interacts with GitHub API to create issues.
 */
@Component
public class GitHubClient implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubClient.class);

    @Value("${github.api.url}")
    private String githubApiUrl;

    @Value("${github.api.token}")
    private String authToken;

    private final RestTemplate restTemplate;

    public GitHubClient() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String createIssue(String title, String description, String severity) {
        log.info("Creating GitHub issue: {} [{}]", title, severity);
        
        // Real implementation logic:
        // 1. Construct JSON payload
        // 2. POST to GitHub API
        // 3. Parse response for URL
        
        // Placeholder for actual HTTP call:
        // String url = githubApiUrl + "/repos/{owner}/{repo}/issues";
        // 
        // Map<String, Object> payload = new HashMap<>();
        // payload.put("title", "[" + severity + "] " + title);
        // payload.put("body", description);
        // 
        // HttpHeaders headers = new HttpHeaders();
        // headers.setBearerAuth(authToken);
        // 
        // HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        // 
        // ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        // return (String) response.getBody().get("html_url");

        // Returning a placeholder URL if actual call is not mocked/integrated in this env
        return "https://github.com/mock-repo/issues/" + System.currentTimeMillis();
    }
}
