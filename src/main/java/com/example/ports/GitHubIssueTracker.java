package com.example.ports;

import java.net.URI;

/**
 * Port Interface for creating issues in a Git tracker (GitHub).
 */
public interface GitHubIssueTracker {

    GitHubIssueResponse createIssue(String repoUrl, String title, String body);

    record GitHubIssueResponse(URI url, String state, String issueId) {}
}
