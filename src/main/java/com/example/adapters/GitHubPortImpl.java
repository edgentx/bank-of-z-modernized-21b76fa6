package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class GitHubPortImpl implements GitHubPort {

    private final RestTemplate restTemplate;

    public GitHubPortImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String createIssue(String title, String body) {
        // Real implementation would use RestTemplate to post to GitHub API
        // returning the URL from the response.
        return "https://github.com/mock-org/repo/issues/1";
    }
}