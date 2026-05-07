package com.example.ports;

import java.net.URI;

/**
 * Port interface for creating GitHub issues.
 */
public interface GitHubIssuePort {
    URI createIssue(String title, String description);
}