package com.example.ports;

/**
 * Port for creating GitHub Issues.
 */
public interface GitHubPort {
    String createIssue(String title, String description, String labels);
}
