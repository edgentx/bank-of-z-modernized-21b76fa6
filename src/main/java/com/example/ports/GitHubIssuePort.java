package com.example.ports;
import java.net.URI;

/**
 * Port for creating GitHub issues.
 */
public interface GitHubIssuePort {
    /**
     * Creates a GitHub issue.
     * @param title The title of the issue.
     * @param body The body content of the issue.
     * @return The URL of the created issue.
     */
    URI createIssue(String title, String body);
}