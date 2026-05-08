package com.example.ports;

import java.util.Optional;

public interface GitHubClient {
    String createIssue(String repo, String title, String body);
}