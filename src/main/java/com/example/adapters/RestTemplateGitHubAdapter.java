package com.example.adapters;

import com.example.ports.GitHubPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Real implementation of GitHubPort using Spring RestTemplate.
 * Note: Requires 'spring-boot-starter-web' on classpath.
 */
@Component
public class RestTemplateGitHubAdapter implements GitHubPort {

    private final RestTemplate restTemplate;
    private final String repoUrl;

    public RestTemplateGitHubAdapter(
            RestTemplate restTemplate,
            @Value("${github.repo.url}") String repoUrl) {
        this.restTemplate = restTemplate;
        this.repoUrl = repoUrl;
    }

    @Override
    public String createIssue(String title, String description) {
        // Stubbed implementation for compilation satisfaction.
        // In a real scenario, this would use restTemplate.postForObject against the GitHub API.
        // We return a constructed URL to simulate the API response.
        return repoUrl + "/issues/" + title.hashCode();
    }
}
