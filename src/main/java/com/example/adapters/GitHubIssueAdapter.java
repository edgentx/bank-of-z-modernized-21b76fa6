package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Real adapter for interacting with GitHub Issues.
 * Uses Spring's RestTemplate for HTTP communication.
 * NOTE: This is a simplified implementation. In a real production environment,
 * you would use a dedicated GitHub client library (e.g., hubspot/github-client) or OkHttp
 * and handle authentication headers and error parsing more robustly.
 */
public class GitHubIssueAdapter implements GitHubIssuePort {

    private static final Logger log = LoggerFactory.getLogger(GitHubIssueAdapter.class);
    private final RestTemplate restTemplate;
    private final String apiUrl;

    public GitHubIssueAdapter(RestTemplate restTemplate, String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    @Override
    public String createIssue(String title, String description) {
        // Construct the JSON payload manually or use a DTO.
        // Using manual Map construction here to minimize boilerplate class count for this task.
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", title);
        requestBody.put("body", description);

        try {
            // Post expects a map response containing the 'html_url'
            Map response = restTemplate.postForObject(apiUrl, requestBody, Map.class);

            if (response != null && response.containsKey("html_url")) {
                String url = (String) response.get("html_url");
                log.info("Successfully created GitHub issue at {}", url);
                return url;
            } else {
                log.error("GitHub API response did not contain html_url");
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to create GitHub issue", e);
            return null;
        }
    }
}
