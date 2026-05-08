package com.example.ports;

/**
 * Port interface for creating GitHub issues.
 */
public interface GitHubIssuePort {
    String createIssue(String title, String body);
}