package com.example.ports;

/**
 * Port for creating GitHub issues.
 */
public interface GitHubPort {
    String createIssue(String defectCode, String summary, String severity);
}
