package com.example.mocks;

import com.example.ports.VForce360IntegrationPort;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of VForce360IntegrationPort for testing.
 * Simulates the defect reporting process without calling external APIs.
 */
public class MockVForce360IntegrationAdapter implements VForce360IntegrationPort {

    private final List<Call> calls = new ArrayList<>();
    private String nextIssueUrl = "https://github.com/mock-repo/issues/1";
    private boolean shouldFail = false;

    public record Call(String title, String body) {}

    @Override
    public String reportDefect(String title, String body) {
        if (shouldFail) {
            throw new RuntimeException("MockIntegration failure");
        }
        calls.add(new Call(title, body));
        return nextIssueUrl;
    }

    public List<Call> getCalls() {
        return calls;
    }

    public void setNextIssueUrl(String url) {
        this.nextIssueUrl = url;
    }

    public void setShouldFail(boolean fail) {
        this.shouldFail = fail;
    }
}