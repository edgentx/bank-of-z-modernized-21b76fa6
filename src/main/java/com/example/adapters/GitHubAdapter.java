package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Real adapter for creating GitHub issues.
 * Implements the GitHubPort interface.
 * Configured via application properties (github.api.url, github.token, github.repo).
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubAdapter.class);
    private final String apiUrl;
    private final String token;
    private final String repo;
    private final RestTemplate restTemplate;

    public GitHubAdapter(@Value("${github.api.url}") String apiUrl,
                         @Value("${github.token}") String token,
                         @Value("${github.repo}") String repo,
                         RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.token = token;
        this.repo = repo;
        this.restTemplate = restTemplate;
    }

    @Override
    public String createIssue(String title, String body) {
        String url = apiUrl + "/repos/" + repo + "/issues";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("title", title);
        payload.put("body", body);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            // We expect the API to return a JSON object containing 'html_url'
            Map response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class).getBody();
            if (response != null && response.containsKey("html_url")) {
                return (String) response.get("html_url");
            }
        } catch (Exception e) {
            log.error("Failed to create GitHub issue", e);
            throw new RuntimeException("GitHub issue creation failed", e);
        }

        throw new RuntimeException("GitHub issue creation failed: invalid response");
    }
}
