package com.example.ports;

import java.util.Optional;

public interface GitHubPort {
    Optional<String> createIssue(String title, String description, String component);
}
