package com.example.mocks;

import com.example.ports.IssueTrackerPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock implementation of IssueTrackerPort for testing.
 * Allows setting a predetermined URL to return, simulating a GitHub response.
 */
public class MockIssueTrackerAdapter implements IssueTrackerPort {

    private String nextUrl;
    private boolean called = false;

    public void setNextUrl(String url) {
        this.nextUrl = url;
    }

    public boolean wasCalled() {
        return called;
    }

    @Override
    public Map<String, String> createIssue(String title, String body) {
        this.called = true;
        
        // Simulate a successful response from GitHub
        Map<String, String> response = new HashMap<>();
        response.put("status", "201");
        
        if (this.nextUrl != null) {
            response.put("url", this.nextUrl);
        } else {
            // Default fallback if test didn't set it
            response.put("url", "https://github.com/bank-of-z/issues/default");
        }
        
        return response;
    }
}
