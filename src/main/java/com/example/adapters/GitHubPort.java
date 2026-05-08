package com.example.adapters;

import java.util.Optional;

/**
 * Port interface for creating GitHub issues.
 */
public interface GitHubPort {
    Optional<String> createIssue(String title, String body);
}
