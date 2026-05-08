package com.example.ports;

import java.util.Optional;

public interface GitHubIssuePort {
    Optional<String> createIssue(String title, String description);
}
