package com.example.ports;

/**
 * Port for interacting with GitHub Issues API.
 */
public interface GitHubPort {
    String createIssue(String title, String body);
}