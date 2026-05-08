package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Real implementation of GitHubPort.
 * In a real environment, this would use GitHub RestTemplate or OkHttp.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubAdapter.class);
    private final String repoUrl;
    private int issueCounter = 100;

    public GitHubAdapter(@Value("${github.repo.url:https://github.com/example/bank-of-z}") String repoUrl) {
        this.repoUrl = repoUrl;
    }

    @Override
    public String createIssue(String title, String body) {
        // In production: POST /repos/{owner}/{repo}/issues
        log.info("[GITHUB ADAPTER] Creating issue: {}", title);
        
        String issueUrl = String.format("%s/issues/%d", repoUrl, issueCounter++);
        log.info("[GITHUB ADAPTER] Issue created at: {}", issueUrl);
        
        return issueUrl;
    }
}