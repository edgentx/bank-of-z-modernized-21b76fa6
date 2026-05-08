package com.example.domain.vforce.port;

import com.example.domain.vforce.model.GitHubIssue;

/**
 * Port interface for GitHub integration.
 * Placed in src/test/java structure here for definition, 
 * but in a real implementation this would be in src/main/java.
 */
public interface GitHubIssuePort {
    GitHubIssue createIssue(String title, String body);
}
