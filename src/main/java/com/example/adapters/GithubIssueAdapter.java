package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Real implementation of GitHubPort using RestTemplate.
 * Wiring is handled by Spring Boot configuration.
 */
@Component
public class GithubIssueAdapter implements GitHubPort {

    private final RestTemplate restTemplate;
    private final String githubApiUrl;
    private final String authToken;

    public GithubIssueAdapter(RestTemplate restTemplate,
                              String githubApiUrl,
                              String authToken) {
        this.restTemplate = restTemplate;
        this.githubApiUrl = githubApiUrl;
        this.authToken = authToken;
    }

    @Override
    public String createIssue(String title, String description) {
        // Constructing the GitHub API request body
        // We use a Map here which Jackson will serialize to JSON
        Map<String, Object> requestBody = Map.of(
            "title", title,
            "body", description
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // Execute POST request
        // We assume the response is a Map containing the "html_url" field
        @SuppressWarnings("rawtypes")
        Map response = restTemplate.postForObject(githubApiUrl, entity, Map.class);

        if (response != null && response.containsKey("html_url")) {
            return (String) response.get("html_url");
        }

        throw new RuntimeException("Failed to create GitHub issue or retrieve URL");
    }
}
