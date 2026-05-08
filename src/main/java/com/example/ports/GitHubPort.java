package com.example.ports;

public interface GitHubPort {
    String createIssue(String repo, String title, String body);
}
