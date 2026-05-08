package com.example.mocks;

import com.example.ports.VForce360Port;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of VForce360Port for testing.
 */
public class MockVForce360Port implements VForce360Port {

    private final List<String> reportedTitles = new ArrayList<>();
    private String mockReturnUrl = "https://github.com/mock-repo/issues/1";
    private boolean shouldFail = false;

    @Override
    public String reportDefect(String defectTitle) {
        reportedTitles.add(defectTitle);
        if (shouldFail) {
            return null; // Simulate failure
        }
        return mockReturnUrl;
    }

    public boolean wasCalledWith(String title) {
        return reportedTitles.contains(title);
    }

    public void setMockReturnUrl(String url) {
        this.mockReturnUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }

    public int getCallCount() {
        return reportedTitles.size();
    }
}
