package com.example.mocks;

import com.example.ports.VForce360Port;
import java.util.Map;
import java.util.HashMap;

/**
 * Mock implementation of VForce360Port for unit testing.
 * Simulates creation of a GitHub issue and returns a predictable URL.
 */
public class MockVForce360Port implements VForce360Port {

    private String nextIssueUrl = "https://github.com/mock-repo/issues/1";
    private int callCount = 0;

    @Override
    public Map<String, String> reportDefect(String projectId, String title, String description, String severity) {
        callCount++;
        // Simulate external service returning a link to the created ticket
        Map<String, String> response = new HashMap<>();
        response.put("id", "GH-" + callCount);
        response.put("url", this.nextIssueUrl + "?id=" + callCount); 
        return response;
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }
}