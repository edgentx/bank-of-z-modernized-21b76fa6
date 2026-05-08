package com.example.mocks;

import com.example.ports.TicketingSystem;

/**
 * Mock implementation of TicketingSystem for testing purposes.
 * Allows controlling the returned URL to simulate success or failure.
 */
public class MockTicketingSystem implements TicketingSystem {

    private String nextUrl = "https://github.com/bank-of-z/issues/1";
    private boolean shouldFail = false;

    public void setNextUrl(String url) {
        this.nextUrl = url;
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public String createIssue(String title, String description) {
        if (shouldFail) {
            return null;
        }
        return nextUrl;
    }
}
