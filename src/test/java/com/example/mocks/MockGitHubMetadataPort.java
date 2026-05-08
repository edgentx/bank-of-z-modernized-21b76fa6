package com.example.mocks;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock adapter for GitHub metadata retrieval.
 * Simulates looking up issue URLs based on defect IDs.
 */
public class MockGitHubMetadataPort {
    private final Map<String, String> defectToUrlMap = new HashMap<>();

    public void mockUrlForDefect(String defectId, String url) {
        defectToUrlMap.put(defectId, url);
    }

    public String getIssueUrl(String defectId) {
        // Simulate behavior: if we know it, return it; otherwise return a default or fail
        return defectToUrlMap.getOrDefault(defectId, "https://github.com/example/issues/unknown");
    }
}
