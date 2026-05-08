package com.example.mocks;

import com.example.ports.VForce360ReportingPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of VForce360ReportingPort for testing.
 * Allows configuring the return value for GitHub URLs.
 */
public class MockVForce360ReportingAdapter implements VForce360ReportingPort {

    private final Map<String, String> mockUrls = new HashMap<>();

    /**
     * Configures the mock to return a specific URL for a given defect ID.
     *
     * @param defectId The ID.
     * @param url      The URL to return.
     */
    public void setMockGitHubUrl(String defectId, String url) {
        this.mockUrls.put(defectId, url);
    }

    @Override
    public String getGitHubIssueUrl(String defectId) {
        // Return configured URL or null to simulate failure/missing data
        return mockUrls.get(defectId);
    }
}