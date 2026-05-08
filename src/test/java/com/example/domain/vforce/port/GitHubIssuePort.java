package com.example.domain.vforce.port;

import com.example.domain.vforce.model.GitHubIssue;

/**
 * Port interface for GitHub integration.
 */
public interface GitHubIssuePort {
    GitHubIssue createIssue(String title, String body);
}
