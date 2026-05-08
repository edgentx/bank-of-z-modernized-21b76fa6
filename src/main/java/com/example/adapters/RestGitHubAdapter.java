package com.example.adapters;

import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Component;

@Component
public class RestGitHubAdapter {
    private final RestTemplate restTemplate;

    public RestGitHubAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String createIssue(String title, String body) {
        // Placeholder
        return "https://github.com/example/repo/issues/1";
    }
}