package com.example.ports;

import java.util.Optional;

/**
 * Port for interacting with GitHub issues.
 * Used by the domain to decouple from specific GitHub client implementation.
 */
public interface GitHubPort {
    Optional<String> createIssue(String title, String description);
}
