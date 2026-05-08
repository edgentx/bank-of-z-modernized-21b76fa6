package com.example.mocks;

import com.example.ports.GitHubPort;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory Mock for GitHub Port.
 * Allows tests to verify correct IDs are being queried and control the returned URL.
 */
public class MockGitHubPort implements GitHubPort {

    private final Map<String, String> urlMapping = new HashMap<>();
    private String lastQueriedId;

    public MockGitHubPort() {
        // Default behavior for the story VW-454
        urlMapping.put("VW-454", "https://github.com/vforce360/issues/454");
    }

    @Override
    public String getIssueUrl(String defectId) {
        this.lastQueriedId = defectId;
        // If not explicitly mapped, return a dummy URL to keep tests predictable
        return urlMapping.getOrDefault(defectId, "https://github.com/vforce360/issues/0");
    }

    public String getLastQueriedId() {
        return lastQueriedId;
    }
}