package com.example.mocks;

import com.example.ports.GitHubIssuePort;

import java.net.URI;
import java.util.Optional;

/**
 * Mock adapter for GitHub interactions.
 * Implements the Port interface to simulate creating issues and generating URLs
 * without hitting the real GitHub API.
 */
public class MockGitHubIssueAdapter implements GitHubIssuePort {

    private static final String FAKE_BASE_URL = "http://github.example.com/mock/";

    @Override
    public URI createIssue(String title, String body) {
        // Simulate a successful creation returning a deterministic mock URL
        return URI.create(FAKE_BASE_URL + "issue/" + System.currentTimeMillis());
    }

    @Override
    public Optional<URI> getIssueUrl(String issueId) {
        // For testing purposes, return a mock URL based on the ID
        if (issueId == null) return Optional.empty();
        return Optional.of(URI.create(FAKE_BASE_URL + "issue/" + issueId));
    }
}
