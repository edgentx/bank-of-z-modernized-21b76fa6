package com.example.application;

/**
 * Port interface for creating GitHub Issues.
 */
public interface GitHubIssuePort {
    String createIssue(String title, String body);
}
