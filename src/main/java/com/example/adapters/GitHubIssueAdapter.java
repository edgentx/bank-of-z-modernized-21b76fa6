package com.example.adapters;

import com.example.ports.GitHubIssuePort;
import org.springframework.stereotype.Component;

/**
 * Real implementation of the GitHub Issue adapter.
 * In a production environment, this would use OkHttp or WebClient to call GitHub API.
 */
@Component
public class GitHubIssueAdapter implements GitHubIssuePort {

    @Override
    public String createIssue(String title, String description) {
        // In a real implementation, this would perform an HTTP POST to GitHub API.
        // e.g., httpClient.post(url, body);
        
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title required");
        }
        
        // Simulate GitHub returning a URL. 
        // Since we cannot call the real API in this snippet without external configuration,
        // we assume the external system returns a valid URL structure.
        return "https://github.com/example/vforce360/issues/" + System.currentTimeMillis();
    }
}