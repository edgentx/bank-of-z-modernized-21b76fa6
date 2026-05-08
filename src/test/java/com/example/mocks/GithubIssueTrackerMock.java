package com.example.mocks;

import com.example.domain.ports.GithubIssueTracker;

/**
 * Mock adapter for GithubIssueTracker.
 * Configurable to return specific URLs or failure states.
 */
public class GithubIssueTrackerMock implements GithubIssueTracker {

    private final String urlToReturn;
    private boolean called = false;

    /**
     * @param urlToReturn The specific URL string this mock should return.
     *                     Pass null to simulate a failure/missing URL.
     */
    public GithubIssueTrackerMock(String urlToReturn) {
        this.urlToReturn = urlToReturn;
    }

    @Override
    public String createIssue(String title, String description) {
        this.called = true;
        // Simulate external call logic
        return this.urlToReturn;
    }

    public boolean wasCalled() {
        return called;
    }
}
