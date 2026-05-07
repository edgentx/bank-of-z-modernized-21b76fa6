package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Real implementation of GitHubPort.
 * In a real scenario, this would use WebClient to call GitHub REST API.
 */
@Component
public class GitHubRestAdapter implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubRestAdapter.class);

    @Override
    public String createRemoteIssue(String defectId, String title, String body) {
        // Simulated API call
        log.info("Creating GitHub issue for {}: {}", defectId, title);
        
        // Real implementation:
        // POST https://api.github.com/repos/bank-of-z/z-force/issues
        // Return: html_url from response
        
        return "https://github.com/bank-of-z/z-force/issues/" + defectId.replace("VW-", "");
    }
}