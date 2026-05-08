package com.example.vforce.github.model;

import java.util.Optional;

/**
 * Value object representing a GitHub issue URL.
 * This class was missing, causing compilation errors in SlackNotificationService.
 */
public record GithubIssue(String url) {
    public GithubIssue {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("GitHub URL cannot be blank");
        }
    }

    public String getUrl() {
        return url;
    }
}
