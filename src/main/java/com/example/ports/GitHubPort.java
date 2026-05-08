package com.example.ports;

/**
 * Port interface for creating GitHub issues.
 */
public interface GitHubPort {
    String createIssue(String repo, String title, String body);
}
