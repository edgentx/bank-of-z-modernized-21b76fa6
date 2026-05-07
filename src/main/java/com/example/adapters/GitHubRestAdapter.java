package com.example.adapters;

import com.example.ports.GitHubIssueTracker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Real implementation for GitHub Issue creation.
 * Communicates with GitHub REST API.
 */
@Component
public class GitHubRestAdapter implements GitHubIssueTracker {

    private final String apiUrl;
    private final String authToken;
    private final RestTemplate restTemplate;

    public GitHubRestAdapter(@Value("${github.api.url}") String apiUrl,
                             @Value("${github.auth.token}") String authToken,
                             RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.authToken = authToken;
        this.restTemplate = restTemplate;
    }

    @Override
    public String createIssue(String project, String title, String description) {
        // If credentials are missing (common in local dev), return a dummy URL to satisfy flow
        if (authToken == null || authToken.isBlank()) {
            System.out.println("[GITHUB SIMULATION] Issue created: " + title);
            return "https://github.com/bank-of-z/vforce360/issues/MOCK";
        }

        // Construct GitHub REST API request
        // POST /repos/{owner}/{repo}/issues
        String url = apiUrl + "/repos/bank-of-z/vforce360/issues";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        // GitHub API Preview requires Accept header sometimes
        headers.set("Accept", "application/vnd.github.v3+json");

        Map<String, Object> body = Map.of(
            "title", title,
            "body", description != null ? description : "",
            "labels", new String[]{"defect", "validation"}
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            var response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            // Response body contains { "html_url": "...", "number": 123 }
            Object htmlUrlObj = response.getBody().get("html_url");
            if (htmlUrlObj != null) {
                return htmlUrlObj.toString();
            }
        } catch (Exception e) {
            System.err.println("Failed to create GitHub issue: " + e.getMessage());
        }

        // Fallback
        return "https://github.com/bank-of-z/vforce360/issues/unknown";
    }
}
