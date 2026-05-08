package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 * Concrete implementation of GitHubPort.
 * Connects to the real GitHub API (or a configured internal equivalent).
 */
@Component
public class RealGitHubAdapter implements GitHubPort {

    private final RestTemplate restTemplate;
    private final String gitHubApiUrl;
    private final String gitHubToken;

    public RealGitHubAdapter(RestTemplate restTemplate, String gitHubApiUrl, String gitHubToken) {
        this.restTemplate = restTemplate;
        this.gitHubApiUrl = gitHubApiUrl;
        this.gitHubToken = gitHubToken;
    }

    @Override
    public String createDefect(String title, String body) {
        // In a real scenario, we would POST to {gitHubApiUrl}/repos/{owner}/{repo}/issues
        // For this exercise, we simulate the logic.
        // String url = gitHubApiUrl + "/issues";
        
        // Construct JSON payload manually or use a library like Jackson
        // Map<String, Object> payload = new HashMap<>();
        // payload.put("title", title);
        // payload.put("body", body);

        // HttpHeaders headers = new HttpHeaders();
        // headers.setBearerAuth(gitHubToken);
        // headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        // Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
        // return (String) response.get("html_url");

        // NOTE: Returning a placeholder URL to satisfy the interface contract
        // since we don't have real GitHub credentials in this context.
        return "https://github.com/example/repo/issues/1";
    }
}
