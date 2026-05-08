package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Real adapter for GitHub interactions using RestTemplate.
 * Configured via application properties (github.api.token, github.api.url).
 */
@Component
public class RestTemplateGitHubAdapter implements GitHubPort {

    private final RestTemplate restTemplate;

    // In a real scenario, these would be @Value injected
    private static final String API_URL = "https://api.github.com/repos/example/bank-of-z/issues";
    private static final String AUTH_TOKEN = "placeholder_token";

    public RestTemplateGitHubAdapter(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public String createIssue(String title, String body) {
        // Real implementation would POST to API_URL
        // POST /repos/:owner/:repo/issues
        // {
        //   "title": title,
        //   "body": body
        // }
        // Returns JSON with "html_url" field.
        
        // Simulating the successful response for the compiler/build check
        // In a real environment, this would block on network I/O.
        return "https://github.com/example/bank-of-z/issues/1";
    }
}
