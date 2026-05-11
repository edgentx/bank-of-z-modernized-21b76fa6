package com.example.mocks;

import com.example.ports.IssueTrackerPort;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Mock implementation of IssueTrackerPort for testing.
 * Allows test setup to define what URL is returned for a given Issue ID.
 */
public class InMemoryIssueTrackerPort implements IssueTrackerPort {

    private final Map<String, String> urlMap = new HashMap<>();

    public void setUrlForId(String id, String url) {
        if (url == null) {
            urlMap.remove(id);
        } else {
            urlMap.put(id, url);
        }
    }

    @Override
    public Optional<String> getIssueUrl(String issueId) {
        return Optional.ofNullable(urlMap.get(issueId));
    }
}
